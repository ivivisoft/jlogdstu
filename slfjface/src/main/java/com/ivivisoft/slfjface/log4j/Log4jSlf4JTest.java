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

package com.ivivisoft.slfjface.log4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log4jSlf4JTest {
    private static Logger logger = LoggerFactory.getLogger(Log4jSlf4JTest.class);

    public static void main(String[] args) {

        if (logger.isDebugEnabled()) {
            logger.debug("slf4j-log4j debug message");
        }
        if (logger.isInfoEnabled()) {
            logger.info("slf4j-log4j info message");
        }
        if (logger.isTraceEnabled()) {
            logger.trace("slf4j-log4j trace message");
        }
    }

}
