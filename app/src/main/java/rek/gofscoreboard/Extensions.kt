package rek.gofscoreboard

fun <E> MutableList<E>.addIfNotNull(element: E?) {
    if (element != null) {
        add(element)
    }
}