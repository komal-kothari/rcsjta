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
package org.gsma.joyn.capability;

import android.net.Uri;

/**
 * Content provider contains capabilities of contacts
 * 
 * @author Jean-Marc AUFFRET
 */
public class CapabilitiesProvider {
    /**
     * Content provider URI
     */
    public static final Uri CONTENT_URI = Uri.parse("content://capabilities");

    /**
     * Capability is not supported
     */
    public static final int NOT_SUPPORTED = 0;

    /**
     * Capability is supported
     */
    public static final int SUPPORTED = 1;

    /**
     * Contains the MSISDN of the contact associated to the capabilities.
     * <P>Type: TEXT</P>
     */
    public static final String COLUMN_CONTACT_NUMBER = "contact_number";

    /**
     * The name of the column containing the image share capability.
     * <P>Type: INTEGER</P>
     */
    public static final String COLUMN_IMAGE_SHARE = "capability_image_share";

    /**
     * The name of the column containing the video share capability.
     * <P>Type: INTEGER</P>
     */
    public static final String COLUMN_VIDEO_SHARE = "capability_video_share";

    /**
     * The name of the column containing the file transfer capability.
     * <P>Type: INTEGER</P>
     */
    public static final String COLUMN_FILE_TRANSFER = "capability_file_transfer";
    
    /**
     * The name of the column containing the chat/IM session capability.
     * <P>Type: INTEGER</P>
     */
    public static final String COLUMN_IM_SESSION = "capability_im_session";
    
    /**
     * The name of the column containing the RCS extensions. List of features tags
     * semicolon separated (e.g. <TAG1>;<TAG2>;�;TAGn).
     * <P>Type: TEXT</P>
     */
    public static final String COLUMN_EXTENSIONS = "capability_extensions";
}
