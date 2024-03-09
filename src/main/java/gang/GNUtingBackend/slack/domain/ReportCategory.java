package gang.GNUtingBackend.slack.domain;

public enum ReportCategory {
    COMMERCIAL_SPAM("영리목적/홍보성"),
    ABUSIVE_LANGUAGE("욕설/인신공격"),
    OBSCENITY("음란성/선정성"),
    FLOODING("도배/반복"),
    PRIVACY_VIOLATION("개인정보노출"),
    OTHER("기타");

    private final String ReportReason;

    ReportCategory(String reportReason) {
        ReportReason = reportReason;
    }
}
