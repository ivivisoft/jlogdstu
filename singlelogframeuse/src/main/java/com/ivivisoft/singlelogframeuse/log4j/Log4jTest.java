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

package com.ivivisoft.singlelogframeuse.log4j;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log4jTest {
    static {
        //log4j file not in classpath,you can load it like this
        //PropertyConfigurator.configure(Loader.getResource("log4j/log4j.properties"));
        //or like this
        PropertyConfigurator.configure(Log4jTest.class.getClassLoader().getResource("log4j/log4j.properties"));
    }
    private static final Logger logger = Logger.getLogger(Log4jTest.class);

    public static void main(String[] args) {
        if (logger.isTraceEnabled()) {
            logger.debug("log4j trace message");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("log4j debug message");
        }
        if (logger.isInfoEnabled()) {
            logger.debug("log4j info message");
        }
    }

}
