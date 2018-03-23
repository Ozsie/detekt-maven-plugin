package sample.plugin

class Plugin {
    private val groupId = ""
    private val artifactId = ""
    private val version = ""
    private val jar = "";

    private val groupIdPath
        get() = groupId.replace(".", "/")

    override fun toString(): String {
        return when (jar) {
            "" -> "$groupIdPath/$artifactId/$version/$artifactId-$version.jar"
            else -> "$groupIdPath/$artifactId/$version/$jar"
        }
    }
}