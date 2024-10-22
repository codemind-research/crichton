package org.crichton.domain.utils.enums;

/**
 * 테스트 결과에 대한 열거형
 */
public enum TestResult {
    None,   // 미정
    Success,   // 성공
    Fail    // 실패
    ;


    /**
     * Returns the name of this enum constant, as contained in the
     * declaration.  This method may be overridden, though it typically
     * isn't necessary or desirable.  An enum class should override this
     * method when a more "programmer-friendly" string form exists.
     *
     * @return the name of this enum constant
     */
    @Override
    public String toString() {
        return switch (this) {
            case None -> "none";
            case Success -> "pass";
            case Fail -> "fail";
        };
    }
}
