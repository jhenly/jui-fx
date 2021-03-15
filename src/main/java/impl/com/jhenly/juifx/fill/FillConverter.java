/** Licensed to the Apache Software Foundation (ASF) under one or more
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
 * the License. */
package impl.com.jhenly.juifx.fill;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import impl.com.jhenly.juifx.fill.BorderFillSpan.BiBorderFillSpan;
import impl.com.jhenly.juifx.fill.FillConverter.BorderFillSpanHalf.QuadBorderFillSpanHalf;
import impl.com.jhenly.juifx.fill.FillConverter.BorderFillSpanHalf.UniBorderFillSpanHalf;
import impl.com.jhenly.juifx.fill.FillSpan.BorderStrokePosition;
import impl.com.jhenly.juifx.util.Utils;
import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.text.Font;


/**
 * Converts fill related values, parsed from CSS, to a {@link Fill} object.
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 */
public final class FillConverter extends StyleConverter<ParsedValue[], Fill> {
    
    private static void outputCssError(String err) { System.err.println("JuiFX CSS parsing error: " + err); }
    
    // lazy, thread-safe instantiation
    private static class Holder {
        static final FillConverter INSTANCE = new FillConverter();
        
        private Holder() { throw new IllegalAccessError("a Holder class should not be instantiated"); }
    }
    /**
     * Gets the {@link FillConverter} singleton converter instance.
     * @return the singleton converter instance
     */
    public static StyleConverter<ParsedValue[], Fill> getInstance() { return Holder.INSTANCE; }
    
    
    @Override
    public Fill convert(Map<CssMetaData<? extends Styleable, ?>, Object> convertedValues) {
        return FillAssembler.assemble(convertedValues.entrySet());
    }
    
    private FillConverter() { super(); }
    
    private static void outErr(String err) { outputCssError("FillConverter - " + err); }
    
    @Override
    public String toString() { return "FillConverter"; }
    
    
    /**************************************************************************
     *                                                                        *
     * Fill Color Converter                                                   *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Converts a fill string to a {@link FillSpanHalf} object.
     */
    public static final class StringConverter extends StyleConverter<String, FillSpanHalf> {
        
        // lazy, thread-safe instantiation
        private static class Holder {
            static final StringConverter INSTANCE = new StringConverter();
            
            private Holder() { throw new IllegalAccessError("a Holder class should not be instantiated"); }
        }
        /**
         * Gets the {@link StringConverter FillConverter.StringConverter}
         * singleton converter instance.
         * @return the singleton converter instance
         */
        public static StringConverter getInstance() { return Holder.INSTANCE; }
        
        /** {@inheritDoc} */
        @Override
        public FillSpanHalf convert(ParsedValue<String, FillSpanHalf> value, Font font) {
            
            Object val = value.getValue();
            if (val == null) { return null; }
            if (val instanceof Color) { return new FillSpanHalf((Color) val); }
            if (val instanceof String) {
                
                String str = ((String) val).strip().toLowerCase(Locale.ENGLISH);
                if (str.isBlank() || "null".equals(str)) { return null; }
                
                return parseStringFast(str);
            }
            
            outErr("could not parse object", val.toString());
            return getErrorHalf();
        }
        
        /**
         * Parses a specified fill string into a {@link FillSpanHalf} object.
         * @param value - the fill string to parse
         * @return a {@code FillSpanHalf} object parsed from the specified
         *         string
         */
        static FillSpanHalf parseString(String value) {
            if (value == null || value.isBlank()) {
                outErr("could not parse null or blank string");
                return getErrorHalf();
            }
            
            final String val = value.strip().toLowerCase(Locale.ENGLISH);
            if ("null".equals(val)) {
                outErr("could not parse \"null\"");
                return getErrorHalf();
            }
            
            return parseStringFast(val);
        }
        
        /** Actual parseString implementation. */
        private static FillSpanHalf parseStringFast(String val) {
            // background can have '[index]' and border can have '[index:pos]'
            final String[] vals = Utils.splitAndStrip(val, '[');
            
            switch (vals[0]) {
                // check if val is a special identifier
                case "text":
                case "txt":
                    return FillSpanHalf.getTextInstance();
                case "shape":
                    return FillSpanHalf.getShapeInstance();
                case "stroke":
                    return FillSpanHalf.getStrokeInstance();
                case "bg":
                case "background":
                    return (vals.length == 1) ? FillSpanHalf.getBgInstance() : parseBgIndex(vals[1]);
                case "bs": // bs, i.e. border stroke
                case "bd":
                case "border":
                    return (vals.length == 1) ? FillSpanHalf.getBorderInstance() : parseBorderString(vals[1]);
                
                // val is not a special identifier, so try to get paint
                default:
                    return parsePaintString(val);
            }
        }
        
        private static FillSpanHalf parsePaintString(String val) {
            // check if val is a linear gradient
            if (val.startsWith("linear")) {
                try {
                    return new FillSpanHalf(LinearGradient.valueOf(val));
                } catch (Exception e) {
                    // fall through
                }
                
                outErr("could not parse linear gradient", val);
                return getErrorHalf();
            }
            
            // check if val is a radial gradient
            if (val.startsWith("radial")) {
                try {
                    return new FillSpanHalf(RadialGradient.valueOf(val));
                } catch (Exception e) {
                    // fall through
                }
                
                outErr("could not parse radial gradient", val);
                return getErrorHalf();
            }
            
            // if we get this far then val must be a color
            try {
                return new FillSpanHalf(Color.web(val));
            } catch (Exception e) {
                // fall through
            }
            
            outErr("could not parse color", val);
            return getErrorHalf();
        }
        
        private static FillSpanHalf parseBgIndex(String index) {
            final int idx;
            
            try {
                idx = parseIndexString(index);
            } catch (Exception e) {
                outErr("could not parse background fill index", Utils.stripTrailingBracket(index));
                return FillSpanHalf.getBgInstance();
            }
            
            if (idx < -1 || idx > 254) {
                outErr("background fill index must be between -1 and 254, received", idx);
                return FillSpanHalf.getBgInstance();
            }
            
            // -1 is the default index
            return (idx != -1) ? new FillSpanHalf(FillSpan.USE_BG, idx) : FillSpanHalf.getBgInstance();
        }
        
        private static FillSpanHalf parseBorderString(String index) {
            String[] pos = Utils.splitAndStrip(index, ':');
            int idx = -1;
            BorderStrokePosition bsPos = null;
            
            // check if we were given a border stroke index or position
            if (isIndexString(pos[0])) {
                // border stroke index was given
                idx = parseBorderIndex(pos[0]);
            } else {
                // border stroke position was given
                bsPos = parseBsPos(pos[0]);
            }
            
            // check if both border stroke index and position were given
            if (pos.length > 1) {
                // border stroke index was given second
                if (isIndexString(pos[1])) {
                    idx = parseBorderIndex(pos[1]);
                } else {
                    // border stroke position was given second
                    bsPos = parseBsPos(pos[1]);
                }
            }
            
            // return default border instance if idx and bsPos are default
            if (idx == -1 && bsPos == null) { return FillSpanHalf.getBorderInstance(); }
            
            return new FillSpanHalf(FillSpan.USE_BORDER, idx, bsPos);
        }
        
        static int parseBorderIndex(String index) {
            final int idx;
            
            try {
                idx = parseIndexString(index);
            } catch (Exception e) {
                outErr("could not parse border stroke index", Utils.stripTrailingBracket(index));
                // -1 is the default index
                return -1;
            }
            
            if (idx < -1 || idx > 254) {
                outErr("border stroke index must be between -1 and 254, received", idx);
                // -1 is the default index
                return -1;
            }
            
            return idx;
        }
        
        static boolean isIndexString(String str) {
            return Character.isDigit(str.charAt(0));
        }
        
        static int parseIndexString(String idxStr) throws NumberFormatException {
            return Integer.parseInt(Utils.stripTrailingBracket(idxStr).strip());
        }
        
        static BorderStrokePosition parseBsPos(String pos) {
            switch (Utils.stripTrailingBracket(pos).strip()) {
                case "t":
                case "top":
                    return BorderStrokePosition.TOP;
                case "r":
                case "right":
                    return BorderStrokePosition.RIGHT;
                case "b":
                case "bottom":
                    return BorderStrokePosition.BOTTOM;
                case "l":
                case "left":
                    return BorderStrokePosition.LEFT;
                
                default:
                    outErr("could not parse border stroke position", pos);
                    return null;
            }
        }
        
        private static void outErr(String err) { outputCssError("FillConverter.StringConverter - " + err); }
        private static void outErr(String err, String value) { outErr(err + " '" + value + "'"); }
        private static void outErr(String err, int value) { outErr(err + " '" + value + "'"); }
        
        private static class ErrorHolder {
            static final FillSpanHalf INSTANCE = new FillSpanHalf(Color.BLACK);
            
            private ErrorHolder() { throw new IllegalAccessError("an ErrorHolder class should not be instantiated"); }
        }
        private static FillSpanHalf getErrorHalf() { return ErrorHolder.INSTANCE; }
        
        @Override
        public String toString() { return "FillConverter.StringConverter"; }
        
    } // class FillColorConverter
    
    
    /**************************************************************************
     *                                                                        *
     * String Sequence Converter                                              *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Converts a fill sequence string into an array of {@link FillSpanHalf}
     * objects.
     */
    public static final class StringSequenceConverter extends StyleConverter<String, FillSpanHalf[]> {
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
        public FillSpanHalf[] convert(ParsedValue<String, FillSpanHalf[]> value, Font font) {
            Object val = value.getValue();
            if (val == null) { return null; }
            
            // if we get a paint then just return an array with that paint
            if (val instanceof Paint) { return new FillSpanHalf[] { new FillSpanHalf((Paint) val) }; }
            
            // if we get a string of paint(s), then parse and return them
            if (val instanceof String) {
                
                final String cleanValue = ((String) val).strip().toLowerCase(Locale.ENGLISH);
                if (cleanValue.isBlank() || "null".equals(val)) { return null; }
                
                final String[] strColors = Utils.splitAndStrip(cleanValue, ',');
                if (strColors.length == 0) { return null; }
                
                return parseSequence(strColors);
            }
            
            outErr("unable to parse fill string sequence");
            return null;
        }
        
        /** Parses array of strings to an array of span halves. */
        private static FillSpanHalf[] parseSequence(String[] seq) {
            FillSpanHalf[] ret = new FillSpanHalf[seq.length];
            for (int i = 0, n = seq.length; i < n; i++) {
                ret[i] = StringConverter.parseString(seq[i]);
            }
            return ret;
        }
        
        private static void outErr(String err) { outputCssError("FillConverter.StringSequenceConverter - " + err); }
        
        @Override
        public String toString() { return "FillConverter.StringSequenceConverter"; }
        
    } // class StringSequenceConverter
    
    
    /**************************************************************************
     *                                                                        *
     * Border String Sequence Converter                                              *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Converts a fill sequence string into an array of {@link FillSpanHalf}
     * objects.
     */
    public static final class BorderStringSequenceConverter extends StyleConverter<String, BorderFillSpanHalf[]> {
        // lazy, thread-safe instantiation
        private static class Holder {
            static final BorderStringSequenceConverter INSTANCE = new BorderStringSequenceConverter();
            
            private Holder() { throw new IllegalAccessError("a Holder class should not be instantiated"); }
        }
        /**
         * Gets the {@link BorderStringSequenceConverter} singleton converter
         * instance.
         * @return the singleton converter instance
         */
        public static BorderStringSequenceConverter getInstance() { return Holder.INSTANCE; }
        
        /** {@inheritDoc} */
        @Override
        public BorderFillSpanHalf[] convert(ParsedValue<String, BorderFillSpanHalf[]> value, Font font) {
            Object val = value.getValue();
            if (val == null) { return null; }
            
            // if we get a color then just return an array with that color
            if (val instanceof Color) {
                return new BorderFillSpanHalf[] { new UniBorderFillSpanHalf(new FillSpanHalf((Color) val)) };
            }
            
            // if we get a string of color(s), then parse and return them
            if (val instanceof String) {
                
                final String cleanValue = ((String) val).strip().toLowerCase(Locale.ENGLISH);
                if (cleanValue.isBlank() || "null".equals(val)) { return null; }
                
                final String[] strBorder = Utils.split(cleanValue, ',');
                if (strBorder.length == 0) { return null; }
                
                return parseBorderSequence(strBorder);
            }
            
            outErr("unable to parse border fill string sequence");
            return null;
        }
        
        /** Parses array of strings to an array of span halves. */
        private static BorderFillSpanHalf[] parseBorderSequence(String[] seq) {
            BorderFillSpanHalf[] ret = new BorderFillSpanHalf[seq.length];
            
            for (int i = 0, n = seq.length; i < n; i++) {
                final String[] borderStrings = Utils.splitWithBrackets(seq[i], ' ', true);
                
                ret[i] = parseBorderStrings(borderStrings);
            }
            return ret;
        }
        
        private static BorderFillSpanHalf parseBorderStrings(String[] borderStrings) {
            
            // account for just getting a single 'border'
            if (borderStrings.length == 1) {
                final String[] seq = borderStrings[0].split("\\[");
                switch (seq[0]) {
                    case "bs":
                    case "bd":
                    case "border":
                        if (seq.length == 1) { return QuadBorderFillSpanHalf.getBorderInstance(); }
                        return parseSingleSequence(seq);
                    default:
                        // not a border string, just a regular fill span half
                        return new UniBorderFillSpanHalf(StringConverter.parseStringFast(seq[0]));
                }
            }
            
            // we got multiple entries
            final FillSpanHalf[] borderHalves = new FillSpanHalf[borderStrings.length];
            
            for (int i = 0; i < borderStrings.length && i < 4; i++) {
                borderHalves[i] = parseSingle(i, borderStrings[i]);
            }
            
            return BorderFillSpanHalf.of(borderHalves);
        }
        
        private static BorderFillSpanHalf parseSingleSequence(String[] seq) {
            final String[] idxAndPos = Utils.stripTrailingBracket(seq[1]).split(":");
            int idx = -1;
            BorderStrokePosition bsPos = null;
            
            if (StringConverter.isIndexString(idxAndPos[0])) {
                idx = StringConverter.parseBorderIndex(idxAndPos[0]);
            } else {
                bsPos = StringConverter.parseBsPos(idxAndPos[0]);
            }
            
            if (idxAndPos.length > 1) {
                if (StringConverter.isIndexString(idxAndPos[1])) {
                    idx = StringConverter.parseBorderIndex(idxAndPos[1]);
                } else {
                    bsPos = StringConverter.parseBsPos(idxAndPos[1]);
                }
            }
            
            if (idx == -1 && bsPos == null) {
                // account for 'bs' in '-fill-border: "bs, bs[1], bs, bs[r];"'
                return QuadBorderFillSpanHalf.getBorderInstance();
            } else if (idx == -1 && bsPos != null) {
                // account for 'bs[POS]' in '-fill-border: "bs[r], bs, bs[1];"'
                final FillSpanHalf tmp = new FillSpanHalf(FillSpan.USE_BORDER, -1, bsPos);
                return new UniBorderFillSpanHalf(tmp);
            } else if (idx != -1 && bsPos == null) {
                // account for 'bs[#]' in '-fill-border: "bs[2], bs, bs[2];"'
                final FillSpanHalf t = new FillSpanHalf(FillSpan.USE_BORDER, idx, BorderStrokePosition.TOP);
                final FillSpanHalf r = new FillSpanHalf(FillSpan.USE_BORDER, idx, BorderStrokePosition.RIGHT);
                final FillSpanHalf b = new FillSpanHalf(FillSpan.USE_BORDER, idx, BorderStrokePosition.BOTTOM);
                final FillSpanHalf l = new FillSpanHalf(FillSpan.USE_BORDER, idx, BorderStrokePosition.LEFT);
                return new QuadBorderFillSpanHalf(t, r, b, l);
            }
            
            // account for 'bs[#:POS]' in '-fill-border: "bs[2:r], bs[t:1], bs[3];"'
            // idx != -1 && bsPos != null
            final FillSpanHalf tmp = new FillSpanHalf(FillSpan.USE_BORDER, idx, bsPos);
            return new UniBorderFillSpanHalf(tmp);
        }
        
        private static FillSpanHalf parseSingle(int i, String single) {
            
            final String[] splitSingle = single.split("\\[");
            switch (splitSingle[0]) {
                // check if it's a border string
                case "bs":
                case "bd":
                case "border":
                    // border string, check if it has any brackets
                    if (splitSingle.length == 1) {
                        switch (i) {
                            case 3:
                                return BorderFillSpanHalf.getLeftBorderInstance();
                            case 2:
                                return BorderFillSpanHalf.getBottomBorderInstance();
                            case 1:
                                return BorderFillSpanHalf.getRightBorderInstance();
                            case 0:
                            default:
                                return BorderFillSpanHalf.getTopBorderInstance();
                        }
                    }
                    
                    // border string with brackets, parse it
                    return parseFillSpanHalf(i, splitSingle);
                
                default:
                    // not a border string, just a regular fill span half
                    return StringConverter.parseStringFast(single);
                
            }
            
        }
        
        private static FillSpanHalf parseFillSpanHalf(int i, String[] seq) {
            final String[] idxAndPos = Utils.stripTrailingBracket(seq[1]).split(":");
            int idx = -1;
            BorderStrokePosition bsPos = null;
            
            if (StringConverter.isIndexString(idxAndPos[0])) {
                idx = StringConverter.parseBorderIndex(idxAndPos[0]);
            } else {
                bsPos = StringConverter.parseBsPos(idxAndPos[0]);
            }
            
            if (idxAndPos.length > 1) {
                if (StringConverter.isIndexString(idxAndPos[1])) {
                    idx = StringConverter.parseBorderIndex(idxAndPos[1]);
                } else {
                    bsPos = StringConverter.parseBsPos(idxAndPos[1]);
                }
            }
            
            if (idx == -1 && bsPos == null) {
                // account for 'bs' in '-fill-border: "bs bs[2] bs[t] bs;"'
                switch (i) {
                    case 3:
                        return BorderFillSpanHalf.getLeftBorderInstance();
                    case 2:
                        return BorderFillSpanHalf.getBottomBorderInstance();
                    case 1:
                        return BorderFillSpanHalf.getRightBorderInstance();
                    case 0:
                    default:
                        return BorderFillSpanHalf.getTopBorderInstance();
                }
            } else if (idx == -1 && bsPos != null) {
                // account for 'bs[POS]' in '-fill-border: "bs[r] bs bs[5] bs[t];"'
                switch (bsPos) {
                    case LEFT:
                        return BorderFillSpanHalf.getLeftBorderInstance();
                    case BOTTOM:
                        return BorderFillSpanHalf.getBottomBorderInstance();
                    case RIGHT:
                        return BorderFillSpanHalf.getRightBorderInstance();
                    case TOP:
                    default:
                        return BorderFillSpanHalf.getTopBorderInstance();
                }
            } else if (idx != -1 && bsPos == null) {
                // account for 'bs[#]' in '-fill-border: "bs[2] bs[1] bs[3] bs[2];"'
                switch (i) {
                    case 3:
                        return new FillSpanHalf(FillSpan.USE_BORDER, idx, BorderStrokePosition.LEFT);
                    case 2:
                        return new FillSpanHalf(FillSpan.USE_BORDER, idx, BorderStrokePosition.BOTTOM);
                    case 1:
                        return new FillSpanHalf(FillSpan.USE_BORDER, idx, BorderStrokePosition.RIGHT);
                    case 0:
                    default:
                        return new FillSpanHalf(FillSpan.USE_BORDER, idx, BorderStrokePosition.TOP);
                }
            }
            
            // account for 'bs[#:POS]' in '-fill-border: "bs[2:r] bs[1] bs[t:3] bs[2];"'
            // idx != -1 && bsPos != null
            return new FillSpanHalf(FillSpan.USE_BORDER, idx, bsPos);
        }
        
        private static void outErr(String err) {
            outputCssError("FillConverter.BorderStringSequenceConverter - " + err);
        }
        
        @Override
        public String toString() { return "FillConverter.BorderStringSequenceConverter"; }
        
    } // class StringSequenceConverter
    
    
    /**************************************************************************
     *                                                                        *
     * Fill Assembler                                                         *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Assembles a {@link Fill} instance via {@link FillAssembler#assemble(Set)}.
     * 
     * @since JuiFX 1.0
     */
    private static class FillAssembler {
        
        /**
         * Assembles a {@link Fill} instance from the specified {@code Set} of
         * converted value entries.
         * @return a {@code Fill} instance assembled from the specified
         *         {@code Set} of converted value entries
         *         
         * @since JuiFX 1.0
         * @apiNote {@code Fill} assembly is done through a {@code static}
         *          method because future versions of JuiFX might cache
         *          {@code Fill} instances with a specified {@code Set} of
         *          converted value entries, if it proves beneficial.
         */
        static Fill assemble(Set<Entry<CssMetaData<? extends Styleable, ?>, Object>> cvEntries) {
            return (new FillAssembler(cvEntries)).assemble();
        }
        
        
        private FillSpan text, shape, stroke;
        private FillSpan[] bgs;
        private BorderFillSpan[] bds;
        
        private FillSpanHalf fText, tText, fShape, tShape, fStroke, tStroke;
        private FillSpanHalf[] fBgs, tBgs;
        private BorderFillSpanHalf[] fBds, tBds;
        
        private FillAssembler(Set<Entry<CssMetaData<? extends Styleable, ?>, Object>> cvEntries) {
            setMembersFromEntries(cvEntries);
            
            checkFromAndTo();
            
            assembleFillSpans();
        }
        
        /**
         * Sets all of the {@code FillSpanHalf} and {@code FillSpanHalf[]}
         * members of this instance using the specified entries from the map of
         * converted values.
         * 
         * @param subProps - entries from map of converted values
         */
        private void setMembersFromEntries(Set<Entry<CssMetaData<? extends Styleable, ?>, Object>> subProps) {
            
            for (Entry<CssMetaData<? extends Styleable, ?>, Object> subProp : subProps) {
                
                Object value = subProp.getValue();
                if (value == null) { continue; }
                
                final String prop = subProp.getKey().getProperty();
                
                if (prop.endsWith(FillCssMetaData.TEXT_FROM)) {
                    fText = (FillSpanHalf) value;
                } else if (prop.endsWith(FillCssMetaData.TEXT_TO)) {
                    tText = (FillSpanHalf) value;
                } else if (prop.endsWith(FillCssMetaData.SHAPE_FROM)) {
                    fShape = (FillSpanHalf) value;
                } else if (prop.endsWith(FillCssMetaData.SHAPE_TO)) {
                    tShape = (FillSpanHalf) value;
                } else if (prop.endsWith(FillCssMetaData.STROKE_FROM)) {
                    fStroke = (FillSpanHalf) value;
                } else if (prop.endsWith(FillCssMetaData.STROKE_TO)) {
                    tStroke = (FillSpanHalf) value;
                } else if (prop.endsWith(FillCssMetaData.BG_FROM)) {
                    fBgs = (FillSpanHalf[]) value;
                } else if (prop.endsWith(FillCssMetaData.BG_TO)) {
                    tBgs = (FillSpanHalf[]) value;
                } else if (prop.endsWith(FillCssMetaData.BORDER_FROM)) {
                    fBds = (BorderFillSpanHalf[]) value;
                } else if (prop.endsWith(FillCssMetaData.BORDER_TO)) {
                    tBds = (BorderFillSpanHalf[]) value;
                }
            }
            
        }
        
        private void checkFromAndTo() {
            // replace null froms with their special identifier, if tos != null
            if (tText != null && fText == null) { fText = FillSpanHalf.getTextInstance(); }
            if (tShape != null && fShape == null) { fShape = FillSpanHalf.getShapeInstance(); }
            if (tStroke != null && fStroke == null) { fStroke = FillSpanHalf.getStrokeInstance(); }
            
            // replace null from bgs with USE_BG, if to bgs != null
            if (tBgs != null && fBgs == null) {
                fBgs = new FillSpanHalf[tBgs.length];
                Arrays.fill(fBgs, FillSpanHalf.getBgInstance());
            }
            
            // replace null from borders with USE_BORDER, if to borders != null
            if (tBds != null && fBds == null) {
                fBds = new BorderFillSpanHalf[tBds.length];
                Arrays.fill(fBds, QuadBorderFillSpanHalf.getBorderInstance());
            }
        }
        
        private void assembleFillSpans() {
            text = (fText == null) ? null : fText.makeWholeWithTo(tText);
            shape = (fShape == null) ? null : fShape.makeWholeWithTo(tShape);
            stroke = (fStroke == null) ? null : fStroke.makeWholeWithTo(tStroke);
            
            bgs = assembleBgFillSpanArray(fBgs, tBgs);
            bds = assembleBdFillSpanArray(fBds, tBds);
        }
        
        private static FillSpan[] assembleBgFillSpanArray(FillSpanHalf[] from, FillSpanHalf[] to) {
            if (from == null && to == null) { return null; }
            
            final int fLen = (from == null) ? 0 : from.length;
            final int tLen = (to == null) ? 0 : to.length;
            final int maxl = Math.max(fLen, tLen);
            final int minl = Math.min(fLen, tLen);
            FillSpan[] ret = new FillSpan[maxl];
            
            // fill from right to left in case from and to differ in length
            for (int i = 0; i < minl; i++) {
                final FillSpanHalf fshFrom = from[(fLen - 1) - i];
                final FillSpanHalf fshTo = to[(tLen - 1) - i];
                
                ret[(maxl - 1) - i] = fshFrom.makeWholeWithTo(fshTo);
            }
            
            // have to account for from and to differing in length
            for (int i = minl; i < maxl; i++) {
                FillSpan fs;
                if (fLen > tLen) {
                    // from is larger than to
                    final FillSpanHalf fshFrom = from[(fLen - 1) - i];
                    // null just indicates to use from for to
                    fs = fshFrom.makeWholeWithTo(null);
                } else {
                    // to is larger than from
                    final FillSpanHalf fshTo = to[(tLen - 1) - i];
                    // use bg or border instance if to was set & from not set
                    fs = fshTo.makeWholeWithFrom(FillSpanHalf.getBgInstance());
                }
                
                ret[(maxl - 1) - i] = fs;
            }
            
            return ret;
        }
        
        private static BorderFillSpan[] assembleBdFillSpanArray(BorderFillSpanHalf[] from, BorderFillSpanHalf[] to) {
            if (from == null && to == null) { return null; }
            
            final int fLen = (from == null) ? 0 : from.length;
            final int tLen = (to == null) ? 0 : to.length;
            final int maxl = Math.max(fLen, tLen);
            final int minl = Math.min(fLen, tLen);
            BorderFillSpan[] ret = new BorderFillSpan[maxl];
            
            // fill from right to left in case from and to differ in length
            for (int i = 0; i < minl; i++) {
                final BorderFillSpanHalf fshFrom = from[(fLen - 1) - i];
                final BorderFillSpanHalf fshTo = to[(tLen - 1) - i];
                
                ret[(maxl - 1) - i] = fshFrom.makeWholeWithTo(fshTo);
            }
            
            // have to account for from and to differing in length
            for (int i = minl; i < maxl; i++) {
                BorderFillSpan fs;
                if (fLen > tLen) {
                    // from is larger than to
                    final BorderFillSpanHalf fshFrom = from[(fLen - 1) - i];
                    // null just indicates to use from for to
                    fs = fshFrom.makeWholeWithTo(null);
                } else {
                    // to is larger than from
                    final BorderFillSpanHalf fshTo = to[(tLen - 1) - i];
                    // use quad border instance if to was set & from not set
                    fs = fshTo.makeWholeWithFrom(QuadBorderFillSpanHalf.getBorderInstance());
                }
                
                ret[(maxl - 1) - i] = fs;
            }
            
            return ret;
        }
        
        private Fill assemble() {
            final Fill ret = new Fill(text, shape, stroke, bgs, bds);
            dispose();
            return ret;
        }
        
        /** Make it easier on the garbage collector. */
        private void dispose() {
            // we don't necessarily need to null everything, but it doesn't hurt
            text = shape = stroke = null;
            bgs = null;
            bds = null;
            fText = tText = fShape = tShape = fStroke = tStroke = null;
            fBgs = tBgs = null;
            fBds = tBds = null;
        }
        
    } // class FillAssembler
    
    
    /**************************************************************************
     *                                                                        *
     * Fill Span Half                                                         *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Two {@code FillSpanHalf} instances together make up one {@link FillSpan}
     * instance.
     * <p>
     * This class should only be used in situations related to CSS parsing.
     * @since JuiFX 1.0
     */
    public static class FillSpanHalf {
        private final boolean isComplex;
        private final Paint paint;
        private final int index;
        private final FillSpan.BorderStrokePosition bsPos;
        
        private FillSpanHalf(Paint p) { this(p, -1, null, false); }
        private FillSpanHalf(Paint p, int i) { this(p, i, null, true); }
        private FillSpanHalf(Paint p, int i, FillSpan.BorderStrokePosition pos) { this(p, i, pos, true); }
        private FillSpanHalf(Paint p, int i, FillSpan.BorderStrokePosition pos, boolean s) {
            paint = p;
            index = i;
            bsPos = pos;
            isComplex = s;
        }
        
        /**
         * Combines this <i>fill-from</i> {@code FillSpanHalf} with its
         * <i>fill-to</i> counterpart to make a whole {@code FillSpan}
         * instance.
         * @param to - this <i>fill-from</i>'s <i>fill-to</i> counterpart
         * @return a whole {@code FillSpan} instance
         */
        private FillSpan makeWholeWithTo(FillSpanHalf to) { return makeWhole(this, (to == null) ? this : to); }
        
        /**
         * Combines this <i>fill-to</i> {@code FillSpanHalf} with its
         * <i>fill-from</i> counterpart to make a whole {@code FillSpan}
         * instance.
         * @param from - this <i>fill-to</i>'s <i>fill-from</i> counterpart
         * @return a whole {@code FillSpan} instance
         */
        private FillSpan makeWholeWithFrom(FillSpanHalf from) {
            return makeWhole((from == null) ? this : from, this);
        }
        
        /**
         * Don't invoke this method directly, use
         * {@link #makeWholeWithTo(FillSpanHalf)} or
         * {@link #makeWholeWithFrom(FillSpanHalf)}, this method does not check
         * for {@code null} parameters.
         * 
         * @param from - the fill-from half
         * @param to - the fill-to half
         * @return a whole {@code FillSpan}
         */
        private static FillSpan makeWhole(FillSpanHalf from, FillSpanHalf to) {
            // neither from nor to has indexes or border strokes
            if (!from.isComplex && !to.isComplex) { return FillSpan.of(from.paint, to.paint); }
            
            final boolean hasIndex = (from.index != -1 || to.index != -1);
            final boolean hasBsPos = (from.bsPos != null || to.bsPos != null);
            
            if (hasIndex && hasBsPos) {
                // from and/or to has both indexes and border strokes
                return FillSpan.of(from.paint, to.paint, from.index, to.index, from.bsPos, to.bsPos);
            } else if (hasIndex) {
                // from and/or to only has indexes
                return FillSpan.of(from.paint, to.paint, from.index, to.index);
            }
            
            // at this point, from and/or to has border strokes and no indexes
            return FillSpan.of(from.paint, to.paint, from.bsPos, to.bsPos);
        }
        
        /**
         * 
         * @param span
         * @return
         */
        static FillSpanHalf getFrom(FillSpan span) {
            if (!(span instanceof SpecialFillSpan)) { return new FillSpanHalf(span.from()); }
            
            SpecialFillSpan sspan = (SpecialFillSpan) span;
            
            if (!sspan.fromIsSpecial()) { return new FillSpanHalf(span.from()); }
            
            final Paint p = sspan.from();
            if (p == FillSpan.USE_TEXT) { return getTextInstance(); }
            if (p == FillSpan.USE_SHAPE) { return getShapeInstance(); }
            if (p == FillSpan.USE_STROKE) { return getStrokeInstance(); }
            
            final int idx = sspan.fromIndex();
            if (p == FillSpan.USE_BG) {
                // use default background instance
                return (idx == -1) ? getBgInstance() : new FillSpanHalf(FillSpan.USE_BG, idx);
            }
            
            final FillSpan.BorderStrokePosition bsPos = FillSpanHelper.getBsPosFromOrdinal(sspan.fromBsPos());
            if (idx == -1 && bsPos == FillSpan.BorderStrokePosition.TOP) {
                // use default border instance
                return getBorderInstance();
            }
            
            return new FillSpanHalf(FillSpan.USE_BORDER, idx, bsPos);
        }
        
        /**
         * 
         * @param span
         * @return
         */
        static FillSpanHalf getTo(FillSpan span) {
            if (!(span instanceof SpecialFillSpan)) { return new FillSpanHalf(span.to()); }
            
            SpecialFillSpan sspan = (SpecialFillSpan) span;
            
            if (!sspan.toIsSpecial()) { return new FillSpanHalf(span.to()); }
            
            final Paint p = sspan.to();
            if (p == FillSpan.USE_TEXT) { return getTextInstance(); }
            if (p == FillSpan.USE_SHAPE) { return getShapeInstance(); }
            if (p == FillSpan.USE_STROKE) { return getStrokeInstance(); }
            
            final int idx = sspan.toIndex();
            if (p == FillSpan.USE_BG) {
                // use default background instance
                return (idx == -1) ? getBgInstance() : new FillSpanHalf(FillSpan.USE_BG, idx);
            }
            
            final FillSpan.BorderStrokePosition bsPos = FillSpanHelper.getBsPosFromOrdinal(sspan.toBsPos());
            if (idx == -1 && bsPos == FillSpan.BorderStrokePosition.TOP) {
                // use default border instance
                return getBorderInstance();
            }
            
            return new FillSpanHalf(FillSpan.USE_BORDER, idx, bsPos);
        }
        
        private static class TextHolder {
            static final FillSpanHalf TEXT = new FillSpanHalf(FillSpan.USE_TEXT);
        }
        private static FillSpanHalf getTextInstance() { return TextHolder.TEXT; }
        
        private static class ShapeHolder {
            static final FillSpanHalf SHAPE = new FillSpanHalf(FillSpan.USE_SHAPE);
        }
        private static FillSpanHalf getShapeInstance() { return ShapeHolder.SHAPE; }
        
        private static class StrokeHolder {
            static final FillSpanHalf STROKE = new FillSpanHalf(FillSpan.USE_STROKE);
        }
        private static FillSpanHalf getStrokeInstance() { return StrokeHolder.STROKE; }
        
        private static class BgHolder {
            static final FillSpanHalf BG = new FillSpanHalf(FillSpan.USE_BG);
        }
        private static FillSpanHalf getBgInstance() { return BgHolder.BG; }
        
        private static class BorderHolder {
            static final FillSpanHalf BORDER = new FillSpanHalf(FillSpan.USE_BORDER);
        }
        private static FillSpanHalf getBorderInstance() { return BorderHolder.BORDER; }
        
    } // class FillSpanHalf
    
    
    /**************************************************************************
     *                                                                        *
     * Border Fill Span Half                                                  *
     *                                                                        *
     *************************************************************************/
    
    /**
     * Two {@code BorderFillSpanHalf} instances together make up one
     * {@link BorderFillSpan} instance.
     * <p>
     * This class should only be used in situations related to CSS parsing.
     * @since JuiFX 1.0
     */
    public static abstract class BorderFillSpanHalf {
        
        protected abstract FillSpanHalf top();
        protected abstract FillSpanHalf right();
        protected abstract FillSpanHalf bottom();
        protected abstract FillSpanHalf left();
        
        protected abstract BorderFillSpan makeWholeWithFrom(UniBorderFillSpanHalf from);
        protected abstract BorderFillSpan makeWholeWithTo(UniBorderFillSpanHalf to);
        protected abstract BorderFillSpan makeWholeWithFrom(BiBorderFillSpanHalf from);
        protected abstract BorderFillSpan makeWholeWithTo(BiBorderFillSpanHalf to);
        protected abstract BorderFillSpan makeWholeWithFrom(QuadBorderFillSpanHalf from);
        protected abstract BorderFillSpan makeWholeWithTo(QuadBorderFillSpanHalf to);
        
        
        static final BorderFillSpanHalf of(FillSpanHalf... halves) {
            if (halves == null || halves.length == 0) { return UniBorderFillSpanHalf.getBorderInstance(); }
            
            switch (halves.length) {
                case 1:
                    if (halves[0] == FillSpanHalf.getBorderInstance()) {
                        return UniBorderFillSpanHalf.getBorderInstance();
                    }
                    return new UniBorderFillSpanHalf(halves[0]);
                case 2:
                case 3:
                    if (halves[0] == getTopBorderInstance() && halves[1] == getRightBorderInstance()) {
                        return BiBorderFillSpanHalf.getBorderInstance();
                    }
                    return new BiBorderFillSpanHalf(halves[0], halves[1]);
                case 4:
                default:
                    if (halves[0] == getTopBorderInstance() && halves[1] == getRightBorderInstance()
                        && halves[2] == getBottomBorderInstance() && halves[3] == getLeftBorderInstance())
                    {
                        return QuadBorderFillSpanHalf.getBorderInstance();
                    }
                    return new QuadBorderFillSpanHalf(halves[0], halves[1], halves[2], halves[3]);
            }
        }
        
        /**
         * 
         * @param span
         * @return
         */
        static BorderFillSpanHalf getFrom(BorderFillSpan span) {
            if (span.isUniform()) {
                return new UniBorderFillSpanHalf(FillSpanHalf.getFrom(span.getTop()));
            } else if (span.getClass() == BiBorderFillSpan.class) {
                final FillSpanHalf t = FillSpanHalf.getFrom(span.getTop());
                final FillSpanHalf r = FillSpanHalf.getFrom(span.getRight());
                
                return new BiBorderFillSpanHalf(t, r);
            } else {
                final FillSpanHalf t = FillSpanHalf.getFrom(span.getTop());
                final FillSpanHalf r = FillSpanHalf.getFrom(span.getRight());
                final FillSpanHalf b = FillSpanHalf.getFrom(span.getBottom());
                final FillSpanHalf l = FillSpanHalf.getFrom(span.getLeft());
                
                return new QuadBorderFillSpanHalf(t, r, b, l);
            }
        }
        
        /**
         * 
         * @param span
         * @return
         */
        static BorderFillSpanHalf getTo(BorderFillSpan span) {
            if (span.isUniform()) {
                return new UniBorderFillSpanHalf(FillSpanHalf.getTo(span.getTop()));
            } else if (span.getClass() == BiBorderFillSpan.class) {
                final FillSpanHalf t = FillSpanHalf.getTo(span.getTop());
                final FillSpanHalf r = FillSpanHalf.getTo(span.getRight());
                
                return new BiBorderFillSpanHalf(t, r);
            } else {
                final FillSpanHalf t = FillSpanHalf.getTo(span.getTop());
                final FillSpanHalf r = FillSpanHalf.getTo(span.getRight());
                final FillSpanHalf b = FillSpanHalf.getTo(span.getBottom());
                final FillSpanHalf l = FillSpanHalf.getTo(span.getLeft());
                
                return new QuadBorderFillSpanHalf(t, r, b, l);
            }
        }
        
        /**
         * Combines this <i>fill-from</i> {@code BorderFillSpanHalf} with its
         * <i>fill-to</i> counterpart to make a whole {@code BorderFillSpan}
         * instance.
         * @param to - this <i>fill-from</i>'s <i>fill-to</i> counterpart
         * @return a whole {@code BorderFillSpan} instance
         */
        private BorderFillSpan makeWholeWithTo(BorderFillSpanHalf to) {
            return makeWhole(this, (to == null) ? this : to);
        }
        
        /**
         * Combines this <i>fill-to</i> {@code BorderFillSpanHalf} with its
         * <i>fill-from</i> counterpart to make a whole {@code BorderFillSpan}
         * instance.
         * @param from - this <i>fill-to</i>'s <i>fill-from</i> counterpart
         * @return a whole {@code BorderFillSpan} instance
         */
        private BorderFillSpan makeWholeWithFrom(BorderFillSpanHalf from) {
            return makeWhole((from == null) ? this : from, this);
        }
        
        /**
         * Don't invoke this method directly, use
         * {@link #makeWholeWithTo(BorderFillSpanHalf)} or
         * {@link #makeWholeWithFrom(BorderFillSpanHalf)}, this method does not
         * check for {@code null} parameters.
         * 
         * @param from - the fill-from half
         * @param to - the fill-to half
         * @return a whole {@code BorderFillSpan}
         */
        private static BorderFillSpan makeWhole(BorderFillSpanHalf from, BorderFillSpanHalf to) {
            final Class<?> fromClass = from.getClass(), toClass = to.getClass();
            BorderFillSpan ret = null;
            
            if (from.getClass() == UniBorderFillSpanHalf.class) {
                from = (UniBorderFillSpanHalf) from;
                
                if (toClass == UniBorderFillSpanHalf.class) {
                    ret = from.makeWholeWithTo((UniBorderFillSpanHalf) to);
                } else if (toClass == BiBorderFillSpanHalf.class) {
                    ret = from.makeWholeWithTo((BiBorderFillSpanHalf) to);
                } else {
                    ret = from.makeWholeWithTo((QuadBorderFillSpanHalf) to);
                }
                
            } else if (fromClass == BiBorderFillSpanHalf.class) {
                from = (BiBorderFillSpanHalf) from;
                
                if (toClass == UniBorderFillSpanHalf.class) {
                    ret = from.makeWholeWithTo((UniBorderFillSpanHalf) to);
                } else if (toClass == BiBorderFillSpanHalf.class) {
                    ret = from.makeWholeWithTo((BiBorderFillSpanHalf) to);
                } else {
                    ret = from.makeWholeWithTo((QuadBorderFillSpanHalf) to);
                }
                
            } else if (fromClass == QuadBorderFillSpanHalf.class) {
                from = (QuadBorderFillSpanHalf) from;
                
                if (toClass == UniBorderFillSpanHalf.class) {
                    ret = from.makeWholeWithTo((UniBorderFillSpanHalf) to);
                } else if (toClass == BiBorderFillSpanHalf.class) {
                    ret = from.makeWholeWithTo((BiBorderFillSpanHalf) to);
                } else {
                    ret = from.makeWholeWithTo((QuadBorderFillSpanHalf) to);
                }
                
            }
            
            return ret;
        }
        
        private static class Holder {
            static final FillSpanHalf TOP = FillSpanHalf.getBorderInstance();
            static final FillSpanHalf RIGHT =
            new FillSpanHalf(FillSpan.USE_BORDER, -1, FillSpan.BorderStrokePosition.RIGHT);
            static final FillSpanHalf BOTTOM =
            new FillSpanHalf(FillSpan.USE_BORDER, -1, FillSpan.BorderStrokePosition.BOTTOM);
            static final FillSpanHalf LEFT =
            new FillSpanHalf(FillSpan.USE_BORDER, -1, FillSpan.BorderStrokePosition.LEFT);
        }
        private static FillSpanHalf getTopBorderInstance() { return Holder.TOP; }
        private static FillSpanHalf getRightBorderInstance() { return Holder.RIGHT; }
        private static FillSpanHalf getBottomBorderInstance() { return Holder.BOTTOM; }
        private static FillSpanHalf getLeftBorderInstance() { return Holder.LEFT; }
        
        
        static class UniBorderFillSpanHalf extends BorderFillSpanHalf {
            
            private static class BorderHolder {
                static final BorderFillSpanHalf INSTANCE = new UniBorderFillSpanHalf(getTopBorderInstance());
            }
            static BorderFillSpanHalf getBorderInstance() { return BorderHolder.INSTANCE; }
            
            protected final FillSpanHalf top;
            
            private UniBorderFillSpanHalf(FillSpanHalf t) { top = t; }
            @Override
            protected FillSpanHalf top() { return top; }
            @Override
            protected FillSpanHalf right() { return top; }
            @Override
            protected FillSpanHalf bottom() { return top; }
            @Override
            protected FillSpanHalf left() { return top; }
            
            @Override
            protected BorderFillSpan makeWholeWithFrom(UniBorderFillSpanHalf from) {
                final FillSpan t = top.makeWholeWithFrom(from.top);
                return BorderFillSpan.of(t);
            }
            @Override
            protected BorderFillSpan makeWholeWithTo(UniBorderFillSpanHalf to) {
                final FillSpan t = top.makeWholeWithTo(to.top);
                return BorderFillSpan.of(t);
            }
            @Override
            protected BorderFillSpan makeWholeWithFrom(BiBorderFillSpanHalf from) {
                return from.makeWholeWithTo(this);
            }
            @Override
            protected BorderFillSpan makeWholeWithTo(BiBorderFillSpanHalf to) {
                return to.makeWholeWithFrom(this);
            }
            @Override
            protected BorderFillSpan makeWholeWithFrom(QuadBorderFillSpanHalf from) {
                return from.makeWholeWithTo(this);
            }
            @Override
            protected BorderFillSpan makeWholeWithTo(QuadBorderFillSpanHalf to) {
                return to.makeWholeWithFrom(this);
            }
            
        } // class UniBorderFillSpanHalf
        
        static class BiBorderFillSpanHalf extends UniBorderFillSpanHalf {
            
            private static class BorderHolder {
                static final BorderFillSpanHalf INSTANCE =
                new BiBorderFillSpanHalf(getTopBorderInstance(), getRightBorderInstance());
            }
            static BorderFillSpanHalf getBorderInstance() { return BorderHolder.INSTANCE; }
            
            protected final FillSpanHalf right;
            
            private BiBorderFillSpanHalf(FillSpanHalf tb, FillSpanHalf rl) { super(tb); right = rl; }
            @Override
            protected FillSpanHalf right() { return right; }
            @Override
            protected FillSpanHalf left() { return right; }
            
            @Override
            protected BorderFillSpan makeWholeWithFrom(UniBorderFillSpanHalf from) {
                final FillSpan tb = top.makeWholeWithFrom(from.top);
                final FillSpan rl = right.makeWholeWithFrom(from.top);
                return BorderFillSpan.of(tb, rl);
            }
            @Override
            protected BorderFillSpan makeWholeWithTo(UniBorderFillSpanHalf to) {
                final FillSpan tb = top.makeWholeWithTo(to.top);
                final FillSpan rl = right.makeWholeWithTo(to.top);
                return BorderFillSpan.of(tb, rl);
            }
            @Override
            protected BorderFillSpan makeWholeWithFrom(BiBorderFillSpanHalf from) {
                final FillSpan tb = top.makeWholeWithFrom(from.top);
                final FillSpan rl = right.makeWholeWithFrom(from.right);
                return BorderFillSpan.of(tb, rl);
            }
            @Override
            protected BorderFillSpan makeWholeWithTo(BiBorderFillSpanHalf to) {
                final FillSpan tb = top.makeWholeWithTo(to.top);
                final FillSpan rl = right.makeWholeWithTo(to.right);
                return BorderFillSpan.of(tb, rl);
            }
            @Override
            protected BorderFillSpan makeWholeWithFrom(QuadBorderFillSpanHalf from) {
                return from.makeWholeWithTo(this);
            }
            @Override
            protected BorderFillSpan makeWholeWithTo(QuadBorderFillSpanHalf to) {
                return to.makeWholeWithFrom(this);
            }
            
        } // class BiBorderFillSpanHalf
        
        static class QuadBorderFillSpanHalf extends BiBorderFillSpanHalf {
            
            private static class BorderHolder {
                static final BorderFillSpanHalf INSTANCE = new QuadBorderFillSpanHalf(getTopBorderInstance(),
                    getRightBorderInstance(), getBottomBorderInstance(), getLeftBorderInstance());
            }
            static BorderFillSpanHalf getBorderInstance() { return BorderHolder.INSTANCE; }
            
            protected final FillSpanHalf bottom, left;
            
            private QuadBorderFillSpanHalf(FillSpanHalf t, FillSpanHalf r, FillSpanHalf b, FillSpanHalf l) {
                super(t, r);
                bottom = b;
                left = l;
            }
            @Override
            protected FillSpanHalf bottom() { return bottom; }
            @Override
            protected FillSpanHalf left() { return left; }
            
            @Override
            protected BorderFillSpan makeWholeWithFrom(UniBorderFillSpanHalf from) {
                final FillSpan t = top.makeWholeWithFrom(from.top);
                final FillSpan r = right.makeWholeWithFrom(from.top);
                final FillSpan b = bottom.makeWholeWithFrom(from.top);
                final FillSpan l = left.makeWholeWithFrom(from.top);
                return BorderFillSpan.of(t, r, b, l);
            }
            @Override
            protected BorderFillSpan makeWholeWithTo(UniBorderFillSpanHalf to) {
                final FillSpan t = top.makeWholeWithTo(to.top);
                final FillSpan r = right.makeWholeWithTo(to.top);
                final FillSpan b = bottom.makeWholeWithTo(to.top);
                final FillSpan l = left.makeWholeWithTo(to.top);
                return BorderFillSpan.of(t, r, b, l);
            }
            @Override
            protected BorderFillSpan makeWholeWithFrom(BiBorderFillSpanHalf from) {
                final FillSpan t = top.makeWholeWithFrom(from.top);
                final FillSpan r = right.makeWholeWithFrom(from.right);
                final FillSpan b = bottom.makeWholeWithFrom(from.top);
                final FillSpan l = left.makeWholeWithFrom(from.right);
                return BorderFillSpan.of(t, r, b, l);
            }
            @Override
            protected BorderFillSpan makeWholeWithTo(BiBorderFillSpanHalf to) {
                final FillSpan t = top.makeWholeWithTo(to.top);
                final FillSpan r = right.makeWholeWithTo(to.right);
                final FillSpan b = bottom.makeWholeWithTo(to.top);
                final FillSpan l = left.makeWholeWithTo(to.right);
                return BorderFillSpan.of(t, r, b, l);
            }
            @Override
            protected BorderFillSpan makeWholeWithFrom(QuadBorderFillSpanHalf from) {
                final FillSpan t = top.makeWholeWithFrom(from.top);
                final FillSpan r = right.makeWholeWithFrom(from.right);
                final FillSpan b = bottom.makeWholeWithFrom(from.bottom);
                final FillSpan l = left.makeWholeWithFrom(from.left);
                return BorderFillSpan.of(t, r, b, l);
            }
            @Override
            protected BorderFillSpan makeWholeWithTo(QuadBorderFillSpanHalf to) {
                final FillSpan t = top.makeWholeWithTo(to.top);
                final FillSpan r = right.makeWholeWithTo(to.right);
                final FillSpan b = bottom.makeWholeWithTo(to.bottom);
                final FillSpan l = left.makeWholeWithTo(to.left);
                return BorderFillSpan.of(t, r, b, l);
            }
            
        } // class QuadBorderFillSpanHalf
        
    } // class BorderFillSpanHalf
    
}
// class FillConverter
