package com.ioabsoftware.gameraven.networking;

public enum NetDesc {
    UNSPECIFIED, MODHIST,
    VERIFY_ACCOUNT_S1, VERIFY_ACCOUNT_S2,
    LOGIN_S1, LOGIN_S2,
    BOARD_JUMPER, GAME_SEARCH, BOARD_LIST, AMP_LIST, TRACKED_TOPICS, BOARD, TOPIC, MESSAGE_DETAIL, USER_DETAIL,
    PM_INBOX, PM_OUTBOX, PM_INBOX_DETAIL, PM_OUTBOX_DETAIL, SEND_PM_S1, SEND_PM_S2,
    MARKMSG, DELETEMSG, CLOSE_TOPIC,
    TAG_USER,
    POSTMSG_S1, POSTMSG_S3,
    POSTTPC_S1, POSTTPC_S3,
    EDIT_MSG
}
