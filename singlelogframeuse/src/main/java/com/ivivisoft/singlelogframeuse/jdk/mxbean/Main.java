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

package com.ivivisoft.singlelogframeuse.jdk.mxbean;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Main {
    static {
        System.setProperty("java.util.logging.config.file", System.getProperty("user.dir") + "/singlelogframeuse/src/main/java/com/ivivisoft/singlelogframeuse/jdk/logging.properties");
    }

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) throws Exception {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName mxbeanName = new ObjectName("com.ivivisoft.jlogdstu.jdk.mxbean:type=PropChange");
        PropChange propChange = new PropChange();
        propChange.setName("andy");
        mBeanServer.registerMBean(propChange,mxbeanName);
        while (true){
            logger.info(propChange.getName());
            TimeUnit.SECONDS.sleep(30);
        }
    }
}
