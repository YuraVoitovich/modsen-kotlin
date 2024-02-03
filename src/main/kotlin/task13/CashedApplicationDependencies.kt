package task13


class CashedApplicationDependencies(private val delegate: ApplicationDependenciesImpl = ApplicationDependenciesImpl())
    : ApplicationDependencies by delegate {

        private var cachedData: String? = null

    override fun fetch(url: String): String {
        if (cachedData == null) {
            this.cachedData = delegate.fetch(url)
        }
        return cachedData ?: ""
    }

}

