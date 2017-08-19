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

import java.util.ArrayList;
import java.util.List;

public class Demo {

    public static void main(String[] args) {
        getClassName("com.ivi.v     com.i.v,,,lkk,,,    ");
        testNULLPoint(null);
    }
    static void getClassName(String classes){
        classes = classes.trim();
        int ix = 0;
        final List<String> result = new ArrayList<>();
        while (ix < classes.length()) {
            int end = ix;
            while (end < classes.length()) {
                if (Character.isWhitespace(classes.charAt(end))) {
                    break;
                }
                if (classes.charAt(end) == ',') {
                    break;
                }
                end++;
            }
            String word = classes.substring(ix, end);
            ix = end+1;
            word = word.trim();
            if (word.length() == 0) {
                continue;
            }
            result.add(word);
        }
        System.out.println(result);
    }

    public static void testNULLPoint(JDKLog jdkLog) {
        //check NEP
        jdkLog.getClass();
    }
}
