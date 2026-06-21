#!/usr/bin/env python3
import sys
import xml.etree.ElementTree as ET
from pathlib import Path


def line_key(file_path, line_number):
    return file_path, int(line_number)


def merge_line(existing, line):
    ci = int(line.attrib.get("ci", 0))
    mi = int(line.attrib.get("mi", 0))
    cb = int(line.attrib.get("cb", 0))
    mb = int(line.attrib.get("mb", 0))

    covered = ci > 0
    branches_to_cover = cb + mb

    if existing is None:
        return {
            "covered": covered,
            "branchesToCover": branches_to_cover,
            "coveredBranches": cb,
        }

    existing["covered"] = existing["covered"] or covered
    existing["branchesToCover"] = max(existing["branchesToCover"], branches_to_cover)
    existing["coveredBranches"] = max(existing["coveredBranches"], cb)
    return existing


def main():
    if len(sys.argv) < 3:
        raise SystemExit(
            "Usage: jacoco-to-sonar-generic.py <output.xml> <jacoco.xml> [<jacoco.xml> ...]"
        )

    output = Path(sys.argv[1])
    reports = [Path(arg) for arg in sys.argv[2:]]
    coverage = {}
    source_roots = [Path("app/src/main/java"), Path("app/src/main/kotlin")]
    source_index = {}
    unmatched = set()

    for source_root in source_roots:
        if source_root.is_dir():
            for source_file in source_root.rglob("*"):
                if source_file.suffix in (".kt", ".java"):
                    source_index.setdefault(source_file.name, []).append(source_file)

    for report in reports:
        root = ET.parse(report).getroot()
        for package in root.findall("package"):
            package_path = package.attrib["name"].replace(".", "/")
            for sourcefile in package.findall("sourcefile"):
                source_name = sourcefile.attrib["name"]
                if not source_name.endswith((".kt", ".java")):
                    continue

                candidates = [
                    source_root / package_path / source_name
                    for source_root in source_roots
                ]
                source_path = next((path for path in candidates if path.is_file()), None)
                if source_path is None:
                    matches = source_index.get(source_name, [])
                    if len(matches) == 1:
                        source_path = matches[0]
                    else:
                        unmatched.add(f"{package_path}/{source_name}")
                        continue

                file_path = source_path.as_posix()

                if file_path.startswith("app/"):
                    file_path = file_path.removeprefix("app/")

                if not Path("app", file_path).is_file():
                    continue

                for line in sourcefile.findall("line"):
                    key = line_key(file_path, line.attrib["nr"])
                    coverage[key] = merge_line(coverage.get(key), line)

    generic_root = ET.Element("coverage", {"version": "1"})
    files = {}
    for (file_path, line_number), data in sorted(coverage.items()):
        file_node = files.get(file_path)
        if file_node is None:
            file_node = ET.SubElement(generic_root, "file", {"path": file_path})
            files[file_path] = file_node

        attrs = {
            "lineNumber": str(line_number),
            "covered": "true" if data["covered"] else "false",
        }
        if data["branchesToCover"] > 0:
            attrs["branchesToCover"] = str(data["branchesToCover"])
            attrs["coveredBranches"] = str(data["coveredBranches"])
        ET.SubElement(file_node, "lineToCover", attrs)

    output.parent.mkdir(parents=True, exist_ok=True)
    ET.ElementTree(generic_root).write(output, encoding="utf-8", xml_declaration=True)

    covered = sum(1 for data in coverage.values() if data["covered"])
    print(
        f"Generated {output} with {len(files)} files, "
        f"{covered}/{len(coverage)} covered lines"
    )
    if unmatched:
        preview = ", ".join(sorted(unmatched)[:10])
        print(f"Unmatched JaCoCo source files: {preview}")
    if covered == 0:
        available_roots = ", ".join(
            f"{root}={'yes' if root.is_dir() else 'no'}" for root in source_roots
        )
        print(f"Source roots: {available_roots}")
        print(f"Indexed source files: {sum(len(files) for files in source_index.values())}")
        raise SystemExit("Generic Sonar coverage report has 0 covered lines")


if __name__ == "__main__":
    main()
