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

package com.ivivisoft.singlelogframeuse.jdk.listenerdemo.dstu;

import java.util.logging.Logger;

public class Test {
    static {
        System.setProperty("java.util.logging.config.file", System.getProperty("user.dir") + "/singlelogframeuse/src/main/java/com/ivivisoft/singlelogframeuse/jdk/logging.properties");
    }

    private static final Logger logger = Logger.getLogger(Test.class.getName());

    public static void main(String[] args) {
        PropertyChange propertyChange = new PropertyChange();
        propertyChange.setName("andy");
        Object ev = Beans.newPropertyChangeEvent(Test.class, "name", propertyChange.getName(), "hello");
        Beans.invokePropertyChange(propertyChange, ev);
        logger.info(propertyChange.getName());
    }
}
