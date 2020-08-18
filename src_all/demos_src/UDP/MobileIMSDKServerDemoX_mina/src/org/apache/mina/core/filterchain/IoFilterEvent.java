/*
 * Copyright (C) 2020  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_X_netty (MobileIMSDK v4.x MINA版) Project. 
 * All rights reserved.
 * 
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * IoFilterEvent.java at 2020-4-14 23:21:11, code by Jack Jiang.
 */
package org.apache.mina.core.filterchain;

import org.apache.mina.core.filterchain.IoFilter.NextFilter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoEvent;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An I/O event or an I/O request that MINA provides for {@link IoFilter}s.
 * Most users won't need to use this class.  It is usually used by internal
 * components to store I/O events.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
// Jack Jiang补充说明：此类来自MINA2.0.13的官方源码（请确保与mina_core.jar中的
// 版本一致！），拷贝过来的目的是自已把MINA的log4j输出代码注释掉，不然那么多日志
// 输出太干扰调试了（这个日志开关是MINA官方做的很不友好的地方，那就自已干掉它）！
public class IoFilterEvent extends IoEvent {
    /** A logger for this class */
    private static final Logger LOGGER = LoggerFactory.getLogger(IoFilterEvent.class);

    /** A speedup for logs */
    private static final boolean DEBUG = LOGGER.isDebugEnabled();

    private final NextFilter nextFilter;

    public IoFilterEvent(NextFilter nextFilter, IoEventType type, IoSession session, Object parameter) {
        super(type, session, parameter);

        if (nextFilter == null) {
            throw new IllegalArgumentException("nextFilter must not be null");
        }

        this.nextFilter = nextFilter;
    }

    public NextFilter getNextFilter() {
        return nextFilter;
    }

    @Override
    public void fire() {
        IoSession session = getSession();
        NextFilter nextFilter = getNextFilter();
        IoEventType type = getType();

//        if (DEBUG) {
//            LOGGER.debug("Firing a {} event for session {}", type, session.getId());
//        }

        switch (type) {
        case MESSAGE_RECEIVED:
            Object parameter = getParameter();
            nextFilter.messageReceived(session, parameter);
            break;

        case MESSAGE_SENT:
            WriteRequest writeRequest = (WriteRequest) getParameter();
            nextFilter.messageSent(session, writeRequest);
            break;

        case WRITE:
            writeRequest = (WriteRequest) getParameter();
            nextFilter.filterWrite(session, writeRequest);
            break;

        case CLOSE:
            nextFilter.filterClose(session);
            break;

        case EXCEPTION_CAUGHT:
            Throwable throwable = (Throwable) getParameter();
            nextFilter.exceptionCaught(session, throwable);
            break;

        case SESSION_IDLE:
            nextFilter.sessionIdle(session, (IdleStatus) getParameter());
            break;

        case SESSION_OPENED:
            nextFilter.sessionOpened(session);
            break;

        case SESSION_CREATED:
            nextFilter.sessionCreated(session);
            break;

        case SESSION_CLOSED:
            nextFilter.sessionClosed(session);
            break;

        default:
            throw new IllegalArgumentException("Unknown event type: " + type);
        }

//        if (DEBUG) {
//            LOGGER.debug("Event {} has been fired for session {}", type, session.getId());
//        }
    }
}
