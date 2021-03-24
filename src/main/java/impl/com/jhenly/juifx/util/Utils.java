package impl.com.jhenly.juifx.util;

/**
 * Class containing all different kinds of utility methods.
 * 
 * @author Jonathan Henly
 * @since JuiFX 1.0
 */
public final class Utils {
    
    /** Don't subclass this utility class. */
    private Utils() {}
    
    /**
     * Method that forces the initialization of a class.
     * @param classToInit - the class to force initialization of
     */
    public static void forceInit(final Class<?> classToInit) {
        try {
            Class.forName(classToInit.getName(), true, classToInit.getClassLoader());
        } catch (final ClassNotFoundException e) {
            throw new AssertionError(e);  // can't happen
        }
    }
    
    /**
     * Removes the last {@code ']'} character, and any characters following it,
     * from a specified string.
     * @param str - the string to strip the trailing bracket from
     * @return the specified string with the trailing bracket and any
     *         characters following it removed
     */
    public static String stripTrailingBracket(String str) {
        if (str == null || str.isEmpty()) { return str; }
        
        final int bracketIdx = str.lastIndexOf(']');
        return (bracketIdx == -1) ? str : str.substring(0, bracketIdx);
    }
    
    /**
     * Splits a specified string into an array of strings, optionally stripped
     * of all whitespace, based on a specified delimiter character.
     * 
     * @param str - the string to split and strip
     * @param delim - the character to split the specified string on
     * @param strip - whether or not to strip all whitespace characters
     * @return an array of split strings, optionally stripped of whitespace
     *         characters
     */
    public static String[] split(String str, char delim, boolean strip) {
        if (str == null || (strip) ? str.isBlank() : str.isEmpty()) { return new String[] {}; }
        
        char[] chars = str.toCharArray();
        String[] ret = {};
        int tail = 0;
        
        for (int i = 0, n = chars.length; i < n; i++) {
            final char cur = chars[i];
            
            if (strip && cur != delim && Character.isWhitespace(cur)) { continue; }
            if (cur == delim) {
                if (tail == 0) { continue; }
                ret = addString(ret, new String(chars, 0, tail));
                tail = 0;
                
                continue;
            }
            
            chars[tail++] = cur;
        }
        
        if (tail > 0) { return addString(ret, new String(chars, 0, tail)); }
        
        return ret;
    }
    
    /**
     * Splits a specified string into an array of strings based on a specified
     * delimiter character.
     * 
     * @param str - the string to split and strip
     * @param delim - the character to split the specified string on
     * @return an array of split strings
     */
    public static String[] split(String str, char delim) { return split(str, delim, false); }
    
    /**
     * Splits a specified string into an array of strings, optionally stripped
     * of all whitespace, based on a specified delimiter character, unless the
     * delimiter falls within a pair of brackets ({@code [...]}).
     * 
     * @param str - the string to split and strip
     * @param delim - the character to split the specified string on
     * @param strip - whether or not to strip all whitespace characters
     * @return an array of split strings, optionally stripped of whitespace
     *         characters
     */
    public static String[] splitWithBrackets(String str, char delim, boolean strip) {
        if (str == null || (strip) ? str.strip().isEmpty() : str.isEmpty()) { return new String[] {}; }
        
        char[] chars = str.toCharArray();
        String[] ret = {};
        int tail = 0;
        boolean inBrackets = false;
        
        for (int i = 0, n = chars.length; i < n; i++) {
            final char cur = chars[i];
            
            if (strip && (inBrackets || cur != delim) && Character.isWhitespace(cur)) { continue; }
            
            if (cur == '[') { inBrackets = true; }
            if (cur == ']') { inBrackets = false; }
            
            if (cur == delim) {
                if (tail == 0) { continue; }
                ret = addString(ret, new String(chars, 0, tail));
                tail = 0;
                
                continue;
            }
            
            chars[tail++] = cur;
        }
        
        if (tail > 0) { return addString(ret, new String(chars, 0, tail)); }
        
        return ret;
    }
    
    /**
     * Splits a specified string into an array of strings, optionally stripped
     * of all whitespace, based on a specified delimiter character, unless the
     * delimiter falls within a pair of brackets ({@code [...]}).
     * 
     * @param str - the string to split and strip
     * @param delim - the character to split the specified string on
     * @param strip - whether or not to strip all whitespace characters
     * @return an array of split strings, optionally stripped of whitespace
     *         characters
     */
    public static String[] splitWithParentheses(String str, char delim, boolean strip) {
        if (str == null || (strip) ? str.strip().isEmpty() : str.isEmpty()) { return new String[] {}; }
        
        char[] chars = str.toCharArray();
        String[] ret = {};
        int tail = 0;
        boolean inParentheses = false;
        
        for (int i = 0, n = chars.length; i < n; i++) {
            final char cur = chars[i];
            
            if (strip && (!inParentheses) && Character.isWhitespace(cur)) { continue; }
            
            if (cur == '(') { inParentheses = true; }
            if (cur == ')') { inParentheses = false; }
            
            if (cur == delim && !inParentheses) {
                if (tail == 0) { continue; }
                ret = addString(ret, new String(chars, 0, tail));
                tail = 0;
                
                continue;
            }
            
            chars[tail++] = cur;
        }
        
        if (tail > 0) { return addString(ret, new String(chars, 0, tail)); }
        
        return ret;
    }
    
    /**
     * Splits a specified string into an array of strings, optionally stripped
     * of all whitespace, based on a specified delimiter character, unless the
     * delimiter falls within a pair of brackets ({@code [...]}).
     * 
     * @param str - the string to split and strip
     * @param delim - the character to split the specified string on
     * @param strip - whether or not to strip all whitespace characters
     * @return an array of split strings, optionally stripped of whitespace
     *         characters
     */
    public static String[] splitWithBracketsAndParentheses(String str, char delim, boolean strip) {
        if (str == null || (strip) ? str.strip().isEmpty() : str.isEmpty()) { return new String[] {}; }
        
        char[] chars = str.toCharArray();
        String[] ret = {};
        int tail = 0;
        boolean inBrackets = false;
        boolean inParentheses = false;
        
        for (int i = 0, n = chars.length; i < n; i++) {
            final char cur = chars[i];
            
            if (strip && !inParentheses && (inBrackets || cur != delim) && Character.isWhitespace(cur)) { continue; }
            
            switch (cur) {
                case '[':
                    if (!inParentheses) { inBrackets = true; }
                    break;
                case '(':
                    if (!inBrackets) { inParentheses = true; }
                    break;
                case ']':
                    inBrackets = false;
                    break;
                case ')':
                    inParentheses = false;
                    break;
                default:
                    break;
            }
            
            if (cur == delim && !inParentheses) {
                if (tail == 0) { continue; }
                ret = addString(ret, new String(chars, 0, tail));
                tail = 0;
                
                continue;
            }
            
            chars[tail++] = cur;
        }
        
        if (tail > 0) { return addString(ret, new String(chars, 0, tail)); }
        
        return ret;
    }
    
    /**
     * Splits a specified string into an array of strings, stripped of all
     * whitespace, based on a specified delimiter character.
     * 
     * @param str - the string to split and strip
     * @param delim - the character to split the specified string on
     * @return an array of split strings stripped of whitespace characters
     */
    public static String[] splitAndStrip(String str, char delim) { return split(str, delim, true); }
    
    /**
     * Splits a specified string into an array of strings, stripped of all
     * whitespace, based on a specified delimiter character and split count
     * limit.
     * 
     * @param str - the string to split and strip
     * @param delim - the character to split the specified string on
     * @param limit - the split count limit, a limit of {@code 0} just returns
     *        an array containing the specified string
     * @return an array of split strings stripped of whitespace characters
     */
    public static String[] splitAndStrip(String str, char delim, int limit) {
        if (str == null || str.isEmpty()) { return new String[] {}; }
        if (limit == 0) { return new String[] { str }; }
        
        char[] chars = str.toCharArray();
        String[] ret = {};
        int tail = 0;
        int cnt = 0;
        
        for (int i = 0, n = chars.length; i < n; i++) {
            final char cur = chars[i];
            
            if (cur != delim && Character.isWhitespace(cur)) { continue; }
            if (cur == delim) {
                if (tail == 0) { continue; }
                
                ret = addString(ret, new String(chars, 0, tail));
                tail = 0;
                
                // add the remaining string to the array and return it
                if (++cnt >= limit) {
                    if (i < n - 1) { return addString(ret, new String(chars, i + 1, n - (i + 1))); }
                    return ret;
                }
                
                continue;
            }
            
            chars[tail++] = cur;
        }
        
        if (tail > 0) { return addString(ret, new String(chars, 0, tail)); }
        
        return ret;
    }
    
    /** Helper method for split*(...) methods. */
    private static String[] addString(String[] arr, String toAdd) {
        String[] ret = new String[arr.length + 1];
        System.arraycopy(arr, 0, ret, 0, arr.length);
        ret[ret.length - 1] = toAdd;
        return ret;
    }
    
}
