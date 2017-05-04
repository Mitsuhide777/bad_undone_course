//package jsonbeans;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.awt.color.ICC_ColorSpace;
import java.util.Objects;

/**
 * Class used for lexical division of json object
 */

class JSONTokenizer {

    // Token types
    private static final int TYPE_INT_CONST = 1;
    private static final int TYPE_SYMBOL = 2;
    private static final int TYPE_IDENTIFIER = 3;

    private static final char LEFT_BRACE = '{';
    private static final char RIGHT_BRACE = '}';
    private static final char LEFT_BRACKET = '[';
    private static final char RIGHT_BRACKET = ']';
    private static final char COLON = ':';
    private static final char COMMA = ',';
    private static final char QUOTE = '\"';
    private static final String KW_CLASS = "class";

    StreamTokenizer tokenizer;

    private Map<Character, Character> reservedSymbols;

    private String currentToken;

    private int tokenType;

    JSONTokenizer(String jsonString) throws JSONDeserializationException {

        StringReader stringReader = new StringReader(jsonString);
        try{
            initializeTokenizer(stringReader);
            getNextToken();
        } catch (IOException | JSONDeserializationException e) {
            JSONError("Intialization problem", 0);
        }
    }

    private void initializeTokenizer(Reader input)
            throws IOException{
        tokenizer = new StreamTokenizer(input);

        tokenizer.parseNumbers();

        tokenizer.whitespaceChars(COMMA, COMMA);
        tokenizer.quoteChar(QUOTE);

        tokenizer.ordinaryChar(LEFT_BRACE);
        tokenizer.ordinaryChar(RIGHT_BRACE);
        tokenizer.ordinaryChar(LEFT_BRACKET);
        tokenizer.ordinaryChar(RIGHT_BRACKET);
        tokenizer.ordinaryChar(COLON);

        initSymbols();
    }

    private void initSymbols() {

        reservedSymbols = new HashMap<>();

        reservedSymbols.put(LEFT_BRACE, LEFT_BRACE);
        reservedSymbols.put(LEFT_BRACKET, LEFT_BRACKET);
        reservedSymbols.put(RIGHT_BRACE, RIGHT_BRACE);
        reservedSymbols.put(RIGHT_BRACKET, RIGHT_BRACKET);
        reservedSymbols.put(COLON, COLON);
        reservedSymbols.put(COMMA, COMMA);
        reservedSymbols.put(QUOTE, QUOTE);
    }

    private void getNextToken() throws JSONDeserializationException {
        if (!hasMoreTokens()) JSONError("Unexpected end of file", tokenizer.lineno());

        try {
            tokenizer.nextToken();
        } catch (IOException e) {
            JSONError("Something went wrong", tokenizer.lineno());
        }

        switch (tokenizer.ttype) {
            case StreamTokenizer.TT_NUMBER:
                tokenType = TYPE_INT_CONST;
                currentToken = String.valueOf(tokenizer.nval);
                break;
            case StreamTokenizer.TT_WORD:
                tokenType = TYPE_IDENTIFIER;
                currentToken = tokenizer.sval;
                break;
            case (int)QUOTE:
                tokenType = TYPE_IDENTIFIER;
                currentToken = tokenizer.sval;
                break;
            default:
                char charToken = (char) tokenizer.ttype;
                if (reservedSymbols.containsValue(charToken)) {
                    tokenType = TYPE_SYMBOL;
                    currentToken = String.valueOf(charToken);
                    break;
                } else {
                    JSONError("Unexpected token: " + charToken, tokenizer.lineno());
                }
        }
    }

    private boolean hasMoreTokens() {
        return tokenizer.ttype != StreamTokenizer.TT_EOF;
    }


    Object readObject() throws JSONDeserializationException {

        if ("null".equals(currentToken)) return null;

        if(!(tokenType == TYPE_SYMBOL && currentToken.equals(String.valueOf(LEFT_BRACE))))
            JSONError("Missing \'{\'", tokenizer.lineno());

        getNextToken();

        if(!(tokenType == TYPE_IDENTIFIER && currentToken.equals(KW_CLASS)))
            JSONError("Missing \"class\" keyword", tokenizer.lineno());

        getNextToken();

        if(!(tokenType == TYPE_SYMBOL && currentToken.equals(String.valueOf(COLON))))
            JSONError("Missing \':\'", tokenizer.lineno());

        getNextToken();

        if(!(tokenType == TYPE_IDENTIFIER))
            JSONError("Missing type identifier", tokenizer.lineno());

        String className = currentToken;

        try{
            Class<?> aClass = Class.forName(className);

/*            Constructor<?>[] constrArr = aClass.getConstructors();
            boolean hasDefault = false;

            for (int i = 0; i < constrArr.length; ++i) {
                if (constrArr[i].getGenericParameterTypes().length == 0)
                {
                    hasDefault = true;
                    break;
                }
            }*/



            Object instance = createInstance(aClass);


//            Object instance = aClass.cast(new Object());
/*            if (hasDefault)
                instance = aClass.newInstance();
            else
            {
                Class<?>[] c = constrArr[0].getParameterTypes();
                Object[] o = new Object[c.length];

                for (int j = 0; j < c.length; ++j) {
                    if (JSONUtil.primitiveSet.contains(c[j]))
                    {
                        if (JSONUtil.numberTypes.contains(c[j]))
                            o[j] = 0;
                        else if (JSONUtil.characterSequenceTypes.contains(c[j]))
                            o[j] = "";
                        else
                            o[j] = false;
                    }
                    else
                        o[j] = null;
                }

                instance = constrArr[0].newInstance(o);
            }*/

            BeanInfo beanInfo = java.beans.Introspector.getBeanInfo(aClass);

            Map<String, PropertyDescriptor> propertyMap = getPropertyMap(beanInfo.getPropertyDescriptors());

            getNextToken();

            while (!(tokenType == TYPE_SYMBOL && currentToken.equals(String.valueOf(RIGHT_BRACE)))){

                if(!(tokenType == TYPE_IDENTIFIER))
                    JSONError("Missing type identifier", tokenizer.lineno());

                PropertyDescriptor property = propertyMap.get(currentToken);

                getNextToken();

                boolean trans = property.getValue("transient") != null;

/*                if (property == null) {
                    getNextToken();

                    if (currentToken.equals("{")) {
                        while (!currentToken.equals("}"))
                            getNextToken();
                        getNextToken();
                    }
                    else if (currentToken.equals("[")) {
                        while (!currentToken.equals("]"))
                            getNextToken();
                        getNextToken();
                    }
                    else
                        getNextToken();
                    continue;
                }*/

                if(!(tokenType == TYPE_SYMBOL && currentToken.equals(String.valueOf(COLON))))
                    JSONError("Missing \':\'", tokenizer.lineno());

                if(JSONUtil.primitiveSet.contains(property.getPropertyType()) && !trans)
                    readPrimitive(property, instance);
                else if(property instanceof IndexedPropertyDescriptor/* && !trans*/)
                    readArray((IndexedPropertyDescriptor) property, instance);
                else{
                    getNextToken();

                    int count = 0;
                    if (currentToken.equals("{"))
                        ++count;

                    try {
                        if (!trans)
                            property.getWriteMethod().invoke(instance, readObject());
                        else {
                            //?
                            if (count == 1)
                                getNextToken();

                            throw new NullPointerException();
                        }
                    } catch (NullPointerException e) {
                        //do nothing
                        try {
                            Object obj = property.getReadMethod().invoke(instance);
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }

                        while (count != 0)
                        {
                            if (currentToken.equals("{"))
                                ++count;

                            if (currentToken.equals("}")) {
                                --count;

                                if (count == 0)
                                    break;
                            }

                            getNextToken();
                        }

//                        break;
                    }
                }


                getNextToken();
            }

            return instance;
        }
        catch (ClassNotFoundException e){
//            JSONError("Class have not found", tokenizer.lineno());
            return null;
        }
        catch (ReflectiveOperationException e){
            JSONError("Unable to instantiate class " + className, tokenizer.lineno());
//            SilentObjectCreator
            return null;
        }
        catch (IntrospectionException e){
            JSONError("Unable to introspect class " + className, tokenizer.lineno());
            return null;
        }
    }

    void readArray(IndexedPropertyDescriptor property, Object invoker) throws JSONDeserializationException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        getNextToken();

        if(!(tokenType == TYPE_SYMBOL && currentToken.equals(String.valueOf(LEFT_BRACKET))))
            JSONError("Missing \'[\'", tokenizer.lineno());
        getNextToken();

        if(JSONUtil.primitiveArraysSet.contains(property.getPropertyType())){
            ArrayList<String> tokensList = new ArrayList<>();

            while (!(tokenType == TYPE_SYMBOL && currentToken.equals(String.valueOf(RIGHT_BRACKET)))){
                tokensList.add(currentToken);

                getNextToken();
            }

            deserializeArray(property, invoker, tokensList);
        }
        else {
            ArrayList<Object> objects = new ArrayList<>();

            while (!(tokenType == TYPE_SYMBOL && currentToken.equals(String.valueOf(RIGHT_BRACKET)))){
                objects.add(property.getIndexedPropertyType().cast(readObject()));
                getNextToken();
            }

            Object[] arr = (Object[]) Array.newInstance(property.getIndexedPropertyType(), objects.size());

            for (int i = 0; i < objects.size(); i++) {
                arr[i] = objects.get(i);
            }

            try {
                property.getWriteMethod().invoke(invoker, (Object) arr);
            } catch (NullPointerException e) {
                //do nothing
            }
        }
    }

    @SuppressWarnings({"Duplicates", "PrimitiveArrayArgumentToVarargsMethod", "ConfusingArgumentToVarargsMethod"})
    void deserializeArray(IndexedPropertyDescriptor property, Object invoker, ArrayList<String> tokensList) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException {

        Class<?> propertyType = property.getPropertyType();
        int arrSize = tokensList.size();

        try {
            if (propertyType == int[].class) {
                int[] arr = new int[arrSize];
                for (int i = 0; i < arrSize; i++) {
                    arr[i] = Double.valueOf(tokensList.get(i)).intValue();
                }
                property.getWriteMethod().invoke(invoker, arr);
            } else if (propertyType == short[].class) {
                short[] arr = new short[arrSize];
                for (int i = 0; i < arrSize; i++) {
                    arr[i] = Double.valueOf(tokensList.get(i)).shortValue();
                }
                property.getWriteMethod().invoke(invoker, arr);
            } else if (propertyType == byte[].class) {
                byte[] arr = new byte[arrSize];
                for (int i = 0; i < arrSize; i++) {
                    arr[i] = Double.valueOf(tokensList.get(i)).byteValue();
                }
                property.getWriteMethod().invoke(invoker, arr);
            } else if (propertyType == long[].class) {
                long[] arr = new long[arrSize];
                for (int i = 0; i < arrSize; i++) {
                    arr[i] = Double.valueOf(tokensList.get(i)).longValue();
                }
                property.getWriteMethod().invoke(invoker, arr);
            } else if (propertyType == double[].class) {
                double[] arr = new double[arrSize];
                for (int i = 0; i < arrSize; i++) {
                    arr[i] = Double.valueOf(tokensList.get(i));
                }
                property.getWriteMethod().invoke(invoker, arr);
            } else if (propertyType == float[].class) {
                float[] arr = new float[arrSize];
                for (int i = 0; i < arrSize; i++) {
                    arr[i] = Double.valueOf(tokensList.get(i)).floatValue();
                }
                property.getWriteMethod().invoke(invoker, arr);
            } else if (propertyType == char[].class) {
                char[] arr = new char[arrSize];
                for (int i = 0; i < arrSize; i++) {
                    arr[i] = tokensList.get(i).charAt(0);
                }
                property.getWriteMethod().invoke(invoker, arr);
            } else if (propertyType == boolean[].class) {
                boolean[] arr = new boolean[arrSize];
                for (int i = 0; i < arrSize; i++) {
                    arr[i] = Boolean.valueOf(tokensList.get(i));
                }
                property.getWriteMethod().invoke(invoker, arr);
            } else if (propertyType == Integer[].class) {
                Integer[] arr = new Integer[arrSize];
                for (int i = 0; i < arrSize; i++) {
                    arr[i] = Double.valueOf(tokensList.get(i)).intValue();
                }
                property.getWriteMethod().invoke(invoker, (Object) arr);
            } else if (propertyType == Byte[].class) {
                Byte[] arr = new Byte[arrSize];
                for (int i = 0; i < arrSize; i++) {
                    arr[i] = Double.valueOf(tokensList.get(i)).byteValue();
                }
                property.getWriteMethod().invoke(invoker, (Object) arr);
            } else if (propertyType == Short[].class) {
                Short[] arr = new Short[arrSize];
                for (int i = 0; i < arrSize; i++) {
                    arr[i] = Double.valueOf(tokensList.get(i)).shortValue();
                }
                property.getWriteMethod().invoke(invoker, (Object) arr);
            } else if (propertyType == Long[].class) {
                Long[] arr = new Long[arrSize];
                for (int i = 0; i < arrSize; i++) {
                    arr[i] = Double.valueOf(tokensList.get(i)).longValue();
                }
                property.getWriteMethod().invoke(invoker, (Object) arr);
            } else if (propertyType == Double[].class) {
                Double[] arr = new Double[arrSize];
                for (int i = 0; i < arrSize; i++) {
                    arr[i] = Double.valueOf(tokensList.get(i));
                }
                property.getWriteMethod().invoke(invoker, (Object) arr);
            } else if (propertyType == Float[].class) {
                Float[] arr = new Float[arrSize];
                for (int i = 0; i < arrSize; i++) {
                    arr[i] = Double.valueOf(tokensList.get(i)).floatValue();
                }
                property.getWriteMethod().invoke(invoker, (Object) arr);
            } else if (propertyType == Character[].class) {
                Character[] arr = new Character[arrSize];
                for (int i = 0; i < arrSize; i++) {
                    arr[i] = tokensList.get(i).charAt(0);
                }
                property.getWriteMethod().invoke(invoker, (Object) arr);
            } else if (propertyType == Boolean[].class) {
                Boolean[] arr = new Boolean[arrSize];
                for (int i = 0; i < arrSize; i++) {
                    arr[i] = Boolean.valueOf(tokensList.get(i));
                }
                property.getWriteMethod().invoke(invoker, (Object) arr);
            } else if (propertyType == String[].class) {
                String[] arr = (String[]) Array.newInstance(String.class, tokensList.size());
                for (int i = 0; i < tokensList.size(); i++) {
                    arr[i] = tokensList.get(i);
                }
                property.getWriteMethod().invoke(invoker, (Object) arr);
            } else if (propertyType == Class[].class) {
                Class[] arr = new Class[arrSize];
                for (int i = 0; i < arrSize; i++) {
                    arr[i] = Class.forName(tokensList.get(i));
                }
                property.getWriteMethod().invoke(invoker, (Object) arr);
            }
        } catch (NullPointerException e) {
            //do nothing
        }
    }

    void readPrimitive(PropertyDescriptor property, Object instance)
            throws ReflectiveOperationException, JSONDeserializationException{

        getNextToken();

        Class<?> propertyType = property.getPropertyType();

        try {
            if (JSONUtil.numberTypes.contains(propertyType)) {

                if (tokenType != TYPE_INT_CONST) JSONError("Wrong value!", tokenizer.lineno());

                //Number value = 0;

                if (propertyType == Byte.class || propertyType == byte.class) {
                    Byte value = Double.valueOf(currentToken).byteValue();
                    property.getWriteMethod().invoke(instance, value);
                }

                else if (propertyType == Short.class || propertyType == short.class) {
                    Short value = Double.valueOf(currentToken).shortValue();
                    property.getWriteMethod().invoke(instance, value);
                }
                else if (propertyType == Integer.class || propertyType == int.class) {
                    Integer value = Double.valueOf(currentToken).intValue();
                    property.getWriteMethod().invoke(instance, value);
                }

                else if (propertyType == Long.class || propertyType == long.class) {
                    Long value = Double.valueOf(currentToken).longValue();
                    property.getWriteMethod().invoke(instance, value);
                }

                else if (propertyType == Float.class || propertyType == float.class) {
                    Float value = Double.valueOf(currentToken).floatValue();
                    property.getWriteMethod().invoke(instance, value);
                }

                else if (propertyType == Double.class || propertyType == double.class) {
                    Double value = Double.valueOf(currentToken);
                    property.getWriteMethod().invoke(instance, value);
                }

//                property.getWriteMethod().invoke(instance, value);
            } else if (JSONUtil.characterSequenceTypes.contains(propertyType)) {
                if (propertyType == Character.class || propertyType == char.class)
                    property.getWriteMethod().invoke(instance, currentToken.charAt(0));
                else property.getWriteMethod().invoke(instance, currentToken);
            } else if (JSONUtil.logicalTypes.contains(propertyType))
                property.getWriteMethod().invoke(instance, Boolean.valueOf(currentToken));
            else if (propertyType == Class.class)
                property.getWriteMethod().invoke(instance, Class.forName(currentToken));
        } catch (NullPointerException e) {
            //do nothing
        }
    }

    private Map<String, PropertyDescriptor> getPropertyMap(PropertyDescriptor[] descriptors) {

        HashMap<String, PropertyDescriptor> propertyDescriptorHashMap = new HashMap<>();

        for(PropertyDescriptor descriptor: descriptors){
            propertyDescriptorHashMap.put(descriptor.getName(), descriptor);
        }

        return propertyDescriptorHashMap;
    }

    private void JSONError(String message, int line) throws JSONDeserializationException {
        throw new JSONDeserializationException(message, line);
    }

    private Object createInstance(Class<?> className)throws InstantiationException, IllegalAccessException, InvocationTargetException,
            NoSuchMethodException
    {
//        className.cast()
        Constructor<?>[] constrArr = className.getDeclaredConstructors();
        boolean hasDefault;

        if (constrArr.length == 0)
            hasDefault = true;
        else
            hasDefault = false;

        for (int i = 0; i < constrArr.length; ++i) {
            if (constrArr[i].getGenericParameterTypes().length == 0)
            {
                hasDefault = true;
                break;
            }
        }

        Object instance = null;

        if (hasDefault) {
            Constructor<?> constr = className.getDeclaredConstructor();
            constr.setAccessible(true);

            instance = constr.newInstance();
        }
        else
        {
//            Constructor<?> con = className.getDeclaredConstructor(Object.class);
            Class<?>[] c = constrArr[0].getParameterTypes();
            Object[] o = new Object[c.length];
//            Class[] obj = new Class[c.length];
            constrArr[0].setAccessible(true);

            for (int j = 0; j < c.length; ++j) {
                if (JSONUtil.primitiveSet.contains(c[j]))
                {
                    if (JSONUtil.numberTypes.contains(c[j]))
                        o[j] = 0;
                    else if (JSONUtil.characterSequenceTypes.contains(c[j]))
                        o[j] = "";
                    else
                        o[j] = false;
                }
                else
                    o[j] = createInstance(c[j]);

//                obj[j] = Object.class;
            }

            instance = constrArr[0].newInstance(o);
        }

        return instance;
    }

}