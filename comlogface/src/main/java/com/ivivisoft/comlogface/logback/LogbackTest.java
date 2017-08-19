/*
 *  Copyright (c) 2016, 张威, ivivisoft@gmail.com
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ivivisoft.comlogface.logback;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogbackTest {
    private static Log logger = LogFactory.getLog(LogbackTest.class);

    public static void main(String[] args) {

        if (logger.isTraceEnabled()) {
            logger.trace("commons-logging-jcl trace message");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("commons-logging-jcl debug message");
        }
        if (logger.isInfoEnabled()) {
            logger.info("commons-logging-jcl info message");
        }
    }
}
