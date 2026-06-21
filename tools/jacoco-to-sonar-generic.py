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

    for report in reports:
        root = ET.parse(report).getroot()
        for package in root.findall("package"):
            package_path = package.attrib["name"]
            for sourcefile in package.findall("sourcefile"):
                source_name = sourcefile.attrib["name"]
                if source_name.endswith((".kt", ".java")):
                    file_path = f"app/src/main/java/{package_path}/{source_name}"
                else:
                    continue
                if not Path(file_path).is_file():
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
    if covered == 0:
        raise SystemExit("Generic Sonar coverage report has 0 covered lines")


if __name__ == "__main__":
    main()
