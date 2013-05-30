/*******************************************************************************
 * Software Name : RCS IMS Stack
 *
 * Copyright (C) 2010 France Telecom S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.orangelabs.rcs.core.ims.protocol.rtp.stream;



import java.net.SocketTimeoutException;

import com.orangelabs.rcs.core.ims.protocol.rtp.core.RtcpPacketReceiver;
import com.orangelabs.rcs.core.ims.protocol.rtp.core.RtcpPacketTransmitter;
import com.orangelabs.rcs.core.ims.protocol.rtp.core.RtcpSession;
import com.orangelabs.rcs.core.ims.protocol.rtp.core.RtpExtensionHeader.ExtensionElement;
import com.orangelabs.rcs.core.ims.protocol.rtp.core.RtpPacket;
import com.orangelabs.rcs.core.ims.protocol.rtp.core.RtpPacketReceiver;
import com.orangelabs.rcs.core.ims.protocol.rtp.format.Format;
import com.orangelabs.rcs.core.ims.protocol.rtp.format.video.VideoOrientation;
import com.orangelabs.rcs.core.ims.protocol.rtp.util.Buffer;
import com.orangelabs.rcs.core.ims.service.richcall.video.VideoSdpBuilder;
import com.orangelabs.rcs.utils.logger.Logger;

/**
 * RTP input stream
 *
 * @author Jean-Marc AUFFRET
 */
public class RtpInputStream implements ProcessorInputStream {
    /**
     * RTP Socket Timeout
     * Used a 20s timeout value because the RTP packets can have a delay
     */
    private static final int RTP_SOCKET_TIMEOUT = 20000;

    /**
     * Remote address
     */
    private String remoteAddress;

    /**
     * Remote port
     */
    private int remotePort;

    /**
     * Local port
     */
    private int localPort;

	/**
	 * RTP receiver
	 */
	private RtpPacketReceiver rtpReceiver =  null;

	/**
	 * RTCP receiver
	 */
	private RtcpPacketReceiver rtcpReceiver =  null;

   /**
     * RTCP transmitter
     */
    private RtcpPacketTransmitter rtcpTransmitter =  null;

    /**
     * Input buffer
     */
	private Buffer buffer = new Buffer();

    /**
     * Input format
     */
	private Format inputFormat = null;

    /**
     * RTCP Session
     */
    private RtcpSession rtcpSession = null;

    /**
     * RTP stream listener
     */
    private RtpStreamListener rtpStreamListener;

    /**
     * The negotiated orientation extension header id
     */
    private int extensionHeaderId = VideoSdpBuilder.DEFAULT_EXTENSION_ID;

	/**
	 * The logger
	 */
	private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Constructor
     *
     * @param localPort Local port
     * @param inputFormat Input format
     */
    public RtpInputStream(String remoteAddress, int remotePort, int localPort, Format inputFormat) {
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
		this.localPort = localPort;
		this.inputFormat = inputFormat;

        rtcpSession = new RtcpSession(false, 16000);
    }

    /**
     * Open the input stream
     *
     * @throws Exception
     */
    public void open() throws Exception {
    	// Create the RTP receiver
        rtpReceiver = new RtpPacketReceiver(localPort, rtcpSession, RTP_SOCKET_TIMEOUT);

    	// Create the RTCP receiver
        rtcpReceiver = new RtcpPacketReceiver(localPort + 1, rtcpSession);
        rtcpReceiver.start();

        // Create the RTCP transmitter
        rtcpTransmitter = new RtcpPacketTransmitter(remoteAddress,
                remotePort + 1,
                rtcpSession,
                rtcpReceiver.getConnection());
        rtcpTransmitter.start();
    }

    /**
     * Close the input stream
     */
    public void close() {
		try {
            // Close the RTCP transmitter
            if (rtcpTransmitter != null)
                rtcpTransmitter.close();

			// Close the RTP receiver
			if (rtpReceiver != null) {
				rtpReceiver.close();
			}

			// Close the RTCP receiver
			if (rtcpReceiver != null) {
				rtcpReceiver.close();
			}
		} catch(Exception e) {
			if (logger.isActivated()) {
				logger.error("Can't close correctly RTP ressources", e);
			}
		}
	}

    /**
     * Returns the RTP receiver
     *
     * @return RTP receiver
     */
    public RtpPacketReceiver getRtpReceiver() {
    	return rtpReceiver;
    }

    /**
     * Returns the RTCP receiver
     *
     * @return RTCP receiver
     */
    public RtcpPacketReceiver getRtcpReceiver() {
        return rtcpReceiver;
    }

    /**
     * Read from the input stream without blocking
     *
     * @return Buffer
     * @throws Exception
     */
    public Buffer read() throws Exception {
        try {
        	// Wait and read a RTP packet
        	RtpPacket rtpPacket = rtpReceiver.readRtpPacket();
        	if (rtpPacket == null) {
        		return null;
        	}
    
        	// Create a buffer
            buffer.setData(rtpPacket.data);
            buffer.setLength(rtpPacket.payloadlength);
            buffer.setOffset(0);
            buffer.setFormat(inputFormat);
        	buffer.setSequenceNumber(rtpPacket.seqnum);
        	buffer.setRTPMarker(rtpPacket.marker!=0);
        	buffer.setTimeStamp(rtpPacket.timestamp);

            if (rtpPacket.extensionHeader != null) {
                ExtensionElement element = rtpPacket.extensionHeader.getElementById(extensionHeaderId);
                if (element != null) {
                    buffer.setVideoOrientation(VideoOrientation.parse(element.data[0]));
                }
            }

        	// Set inputFormat back to null
        	inputFormat = null;
        	return buffer;
        } catch (SocketTimeoutException ex) {
            if (logger.isActivated()) {
                logger.error("RTP Packet receiver socket error", ex);
            }
            if (rtpStreamListener != null) {
                rtpStreamListener.rtpStreamAborted();
            }
            return null;
        }
    }

    /**
     * Adds the RTP stream listener
     *
     * @param rtpStreamListener
     */
    public void addRtpStreamListener(RtpStreamListener rtpStreamListener) {
        this.rtpStreamListener = rtpStreamListener;
    }

    /**
     * Sets the negotiated orientation extension header id
     *
     * @param extensionHeaderId Header id
     */
    public void setExtensionHeaderId(int extensionHeaderId) {
        this.extensionHeaderId = extensionHeaderId;
    }

}
