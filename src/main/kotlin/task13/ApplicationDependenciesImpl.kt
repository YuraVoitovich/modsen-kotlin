package task13

class ApplicationDependenciesImpl : ApplicationDependencies {

    override fun fetch(url: String): String {
        return "fetching data using network"
    }
}