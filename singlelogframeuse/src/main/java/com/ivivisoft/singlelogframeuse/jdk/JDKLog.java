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

package com.ivivisoft.singlelogframeuse.jdk;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDKLog {
    static {
        System.setProperty("java.util.logging.config.file", System.getProperty("user.dir") + "/singlelogframeuse/src/main/java/com/ivivisoft/singlelogframeuse/jdk/logging.properties");
    }

    private static final Logger logger = Logger.getLogger(JDKLog.class.getName());

    public static void main(String[] args) throws IOException {
        //if you don't log4j anything you only got this line logging
        logger.info("Main method running!");
        logger.fine("doing stuff");
        try {
            TimeUnit.SECONDS.sleep(60);
            logger.fine("done");
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "trouble sneezing", e);
        }
    }

}
