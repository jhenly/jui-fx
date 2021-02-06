/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.jhenly.juifx.fill;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 * Converts the CSS 
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 */
public class FillConverter extends StyleConverter<ParsedValue[], Fill> {
    
    public static final StyleConverter<ParsedValue[], Fill> INSTANCE = new FillConverter();
    
    // lazy, thread-safe instantiation
    private static class Holder {
        static final FillColorConverter FILL_COLOR_INSTANCE = new FillColorConverter();
        static final StringSequenceConverter STRING_SEQUENCE_INSTANCE = new StringSequenceConverter();
        static final SequenceConverter SEQUENCE_INSTANCE = new SequenceConverter();
        static final Color PARSE_ERROR_COLOR = new Color(0.0, 0.0, 0.0, 1.0); // Color.BLACK
        static final Color[] NULL_EMPTY = new Color[] {};
        
        private Holder() { throw new IllegalAccessError("a Holder class should not be instantiated"); }
    }
    /**
     * Gets the {@link FillConverter} singleton converter instance.
     * @return the singleton converter instance
     */
    public static StyleConverter<ParsedValue[], Fill> getInstance() { return INSTANCE; }
    
    @Override
    public Fill convert(Map<CssMetaData<? extends Styleable, ?>, Object> convertedValues) {
        
        Color[] fillFroms = (Color[]) convertedValues.get(Fill.FILL_FROM);
        Color[] fillTos = (Color[]) convertedValues.get(Fill.FILL_TO);
        
        System.out.println("fillFroms: " + fillFroms + "  fillFroms == NULL_EMPTY: "
            + (fillFroms == StringSequenceConverter.ErrorHolder.NULL_EMPTY));
        
        final boolean hasFroms = (fillFroms != null && fillFroms.length != 0);
        final boolean hasTos = (fillTos != null && fillTos.length != 0);
        
        // use USE_BG if 'to' colors were specified but 'from' colors were not
        if (!hasFroms && hasTos) {
            fillFroms = new Color[fillTos.length];
            Arrays.fill(fillFroms, FillSpan.USE_BG);
        }
        
        return new Fill(FillSpan.of(fillFroms, fillTos));
    }
    
    private FillConverter() {}
    
    @Override
    public String toString() { return "FillConverter"; }
    
    
    /**************************************************************************
     *                                                                        *
     * Fill Color Converter                                                   *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Converts a string to a Color object.
     */
    public static final class FillColorConverter extends StyleConverter<String, Color> {
        
        // lazy, thread-safe instantiation
        public static StyleConverter<String, Color> getInstance() { return Holder.FILL_COLOR_INSTANCE; }
        
        @Override
        public Color convert(ParsedValue<String, Color> value, Font font) {
            
            Object val = value.getValue();
            if (val == null) { return null; }
            if (val instanceof Color) { return (Color) val; }
            if (val instanceof String) {
                String str = (String) val;
                if (str.isEmpty() || "null".equals(str)) { return null; }
                try {
                    return Color.web((String) val);
                } catch (IllegalArgumentException iae) {
                    // fall through pending RT-34551
                }
            }
            // pending RT-34551
            System.err.println("not a color: " + value);
            return Color.BLACK;
        }
        
        @Override
        public String toString() { return "FillConverter.FillColorConverter"; }
        
    } // class FillColorConverter
    
    
    /**************************************************************************
     *                                                                        *
     * String Sequence Converter                                              *
     *                                                                        *
     *************************************************************************/
    
    /** Converts a string of colors to an array of Color objects. */
    public static final class StringSequenceConverter extends StyleConverter<String, Color[]> {
        // lazy, thread-safe instantiation
        private static class Holder {
            static final StringSequenceConverter INSTANCE = new StringSequenceConverter();
            
            private Holder() { throw new IllegalAccessError("a Holder class should not be instantiated"); }
        }
        /**
         * Gets the {@link StringSequenceConverter} singleton converter
         * instance.
         * @return the singleton converter instance
         */
        public static StringSequenceConverter getInstance() { return Holder.INSTANCE; }
        
        /** {@inheritDoc} */
        @Override
        public Color[] convert(ParsedValue<String, Color[]> value, Font font) {
            Object val = value.getValue();
            if (val == null) { return ErrorHolder.NULL_EMPTY; }
            
            // if we get a color then just return an array with that color
            if (val instanceof Color) { return new Color[] { (Color) val }; }
            
            // if we get a string of color(s), then parse and return them
            if (val instanceof String) {
                
                final String cleanValue = ((String) val).strip().toLowerCase(Locale.ENGLISH);
                if (cleanValue.isEmpty()) { return ErrorHolder.NULL_EMPTY; }
                
                final String[] strColors = cleanValue.split("[\\s,]+");
                if (strColors.length == 0) { return ErrorHolder.NULL_EMPTY; }
                
                return parseColors(strColors, cleanValue);
            }
            
            System.err.println("JuiFX CSS error, unable to parse color(s)");
            return ErrorHolder.NULL_EMPTY;
        }
        
        /** Parses array of color strings to an array of colors. */
        private Color[] parseColors(final String[] strColors, final String cleanValue) {
            Color[] colors = new Color[strColors.length];
            
            for (int i = 0; i < colors.length; i++) {
                final String c = strColors[i];
                if (c.equals("background") || c.equals("inherit")) {
                    colors[i] = FillSpan.USE_BG;
                } else {
                    colors[i] = parseColor(c, cleanValue);
                }
            }
            
            return colors;
        }
        
        /** Returns a color from a string, or returns PARSE_ERROR_COLOR. */
        private Color parseColor(final String color, final String cleanValue) {
            try {
                return Color.web('#' + color);
            } catch (Exception e) {
                System.err.println("JuiFX CSS error, could not parse color '#" + color + "' in '" + cleanValue + "'");
                return ErrorHolder.PARSE_ERROR_COLOR;
            }
        }
        
        // lazy, thread-safe instantiation
        private static class ErrorHolder {
            static final Color[] NULL_EMPTY = new Color[] {};
            static final Color PARSE_ERROR_COLOR = new Color(0.0, 0.0, 0.0, 1.0); // Color.BLACK
            
            private ErrorHolder() { throw new IllegalAccessError("an ErrorHolder class should not be instantiated"); }
        }
        
        @Override
        public String toString() { return "FillConverter.StringSequenceConverter"; }
        
    } // class StringSequenceConverter
    
    
    /**************************************************************************
     *                                                                        *
     * Sequence Converter                                                     *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Converts an array of parsed values to an array of Color objects.
     */
    public static final class SequenceConverter extends StyleConverter<Paint[], Paint[]> {
        
        public static SequenceConverter getInstance() { return Holder.SEQUENCE_INSTANCE; }
        
        private SequenceConverter() {
            // super();
        }
        
        @Override
        public Color[] convert(ParsedValue<Paint[], Paint[]> value, Font font) {
            System.out.println(value);
            
            Paint[] values = value.getValue();
            System.out.println(values);
            
            Color[] colors = new Color[values.length];
            
            for (int p = 0; p < values.length; p++) {
                
                System.out.println(values[p]);
//                colors[p] = values[p].convert(font);
                System.out.println(colors[p]);
            }
            
            return colors;
        }
        
        @Override
        public String toString() {
            return "FillConverter.SequenceConverter";
        }
        
    } // class SequenceConverter
    
    
} // class FillConverter
