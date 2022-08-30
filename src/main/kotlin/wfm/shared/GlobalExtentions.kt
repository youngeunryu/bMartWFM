package wfm.shared

interface GlobalExtentions {
    fun <R> validate(block: () -> R): R = block()
    fun <T, R> T.find(block: (T) -> R): R = block(this)
    fun <R> find(block: () -> R): R = block()
    fun <T, R> T.delete(block: (T) -> R): T = this.also { block(this) }
    fun <T, R> T.save(block: (T) -> R): R = block(this)
    fun <T, R> T.map(block: (T) -> R): R = block(this)
    fun <T, R> T.justRuns(block: (T) -> R): T = this.also { block(this) }
}
