package wfm.shared.excel

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

const val MAXIMUM_LANGUAGE_CODE_LENGTH: Int = 2
const val MAXIMUM_LANGUAGE_NAME_LENGTH: Int = 24
private val ISO_LANGUAGES: Array<String> = Locale.getISOLanguages()

@Entity
class TestEntity(
    @Column(name = "`code`", unique = true, nullable = false, length = MAXIMUM_LANGUAGE_CODE_LENGTH)
    val code: String,
    name: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
) {
    @Column(name = "`name`", nullable = false, length = MAXIMUM_LANGUAGE_NAME_LENGTH)
    private var _name: String = name
    val name: String
        get() = _name

    init {
        validateNameLength(name)
        require(code in ISO_LANGUAGES) { "언어 코드 체계는 ISO 639-1을 따라야 합니다." }
    }

    fun updateName(name: String) {
        require(this.name !== name) { throw IllegalArgumentException("변경 사항이 없습니다") }
        validateNameLength(name)
        this._name = name
    }

    private fun validateNameLength(name: String) {
        require(name.length <= MAXIMUM_LANGUAGE_NAME_LENGTH) { "언어 이름은 ${MAXIMUM_LANGUAGE_NAME_LENGTH}자 이하여야 합니다." }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TestEntity

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
