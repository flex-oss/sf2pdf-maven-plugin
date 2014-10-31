/**
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.cdlflex.sf2pdf.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A helper class for formatting dates and durations.
 */
public final class Format {

    private Format() {
    }

    /**
     * Returns the date in yyyy-MM-dd format.
     * 
     * @param date the {@link Date} to format.
     * @return a formatted string in yyy-MM-dd form.
     */
    public static String date(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    /**
     * Returns a duration in HH:mm:ss format.
     * 
     * @param duration the {@code duration} to format as {@code float}.
     * @return a formatted string in HH:mm:ss form.
     */
    public static String duration(float duration) {
        long l = (long) (duration * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return "" + sdf.format(new Date(l - TimeZone.getDefault().getRawOffset()));
    }
}
