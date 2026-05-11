package ca.soccer1992.lavaproxy.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LazilyParsedNumber;
import net.kyori.adventure.nbt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NBTUtil {

    public static JsonElement deserialize(BinaryTag tag, boolean fixEmptyKeys){

        switch (tag.type().id()){
            // Simple Methods (.value())
            // byte
            case 1: return new JsonPrimitive(((ByteBinaryTag) tag).value());
            // short
            case 2: return new JsonPrimitive(((ShortBinaryTag) tag).value());
            // int
            case 3: return new JsonPrimitive(((IntBinaryTag) tag).value());
            // byte
            case 4: return new JsonPrimitive(((LongBinaryTag) tag).value());
            // float
            case 5: return new JsonPrimitive(((FloatBinaryTag) tag).value());
            // double
            case 6: return new JsonPrimitive(((DoubleBinaryTag) tag).value());
            // string
            case 8: return new JsonPrimitive(((StringBinaryTag) tag).value());
            // Array Methods

            // byte array
            case 7:
                byte[] byteArray = ((ByteArrayBinaryTag) tag).value();

                JsonArray jsonByteArray = new JsonArray(byteArray.length);
                for (byte b : byteArray) {
                    jsonByteArray.add(new JsonPrimitive(b));
                }

                return jsonByteArray;
            // Generic array
            case 9:
                ListBinaryTag items = (ListBinaryTag) tag;
                JsonArray jsonList = new JsonArray(items.size());
                for (BinaryTag subTag : items) {
                    jsonList.add(deserialize(subTag, fixEmptyKeys)); /* recurse on array
                    (yea if the server sends a malformed payload this CAN StackOverflowException, but its very unlikely.)
                    */
                }
                return jsonList;
            // int array
            case 11:
                int[] intArray = ((IntArrayBinaryTag) tag).value();

                JsonArray jsonIntArray = new JsonArray(intArray.length);
                for (int i : intArray) {
                    jsonIntArray.add(new JsonPrimitive(i));
                }

                return jsonIntArray;
            // long array
            case 12:
                long[] longArray = ((LongArrayBinaryTag) tag).value();

                JsonArray jsonlongArray = new JsonArray(longArray.length);
                for (long l : longArray) {
                    jsonlongArray.add(new JsonPrimitive(l));
                }

                return jsonlongArray;
            // Special Compound NBT
            case 10:
                CompoundBinaryTag cmp = (CompoundBinaryTag) tag;
                JsonObject obj = new JsonObject();
                cmp.keySet().forEach(k -> {
                    String realKey = k;
                    if (realKey.isEmpty() && fixEmptyKeys) realKey = "text";
                    obj.add(realKey, deserialize(Objects.requireNonNull(cmp.get(realKey)), fixEmptyKeys)); /* recurse on value
                    (yea if the server sends a malformed payload this CAN StackOverflowException, but its very unlikely.)
                    */

                });
                return obj;

            default: throw new IllegalArgumentException("Invalid TAG id: " + tag.type().id());
        }
    }
    // This was taken from velocity.
    public static BinaryTag serialize(JsonElement json) {
        if (json instanceof JsonPrimitive jsonPrimitive) {
            if (jsonPrimitive.isNumber()) {
                final Number number = json.getAsNumber();

                return switch (number) {
                    case Byte b -> ByteBinaryTag.byteBinaryTag(b);
                    case Short s -> ShortBinaryTag.shortBinaryTag(s);
                    case Integer i -> IntBinaryTag.intBinaryTag(i);
                    case Long l -> LongBinaryTag.longBinaryTag(l);
                    case Float f -> FloatBinaryTag.floatBinaryTag(f);
                    case Double d -> DoubleBinaryTag.doubleBinaryTag(d);
                    case LazilyParsedNumber l -> IntBinaryTag.intBinaryTag(l.intValue());
                    default -> throw new IllegalArgumentException("Unknown number type: " + number);
                };
            } else if (jsonPrimitive.isString()) {
                return StringBinaryTag.stringBinaryTag(jsonPrimitive.getAsString());
            } else if (jsonPrimitive.isBoolean()) {
                return ByteBinaryTag.byteBinaryTag((byte) (jsonPrimitive.getAsBoolean() ? 1 : 0));
            } else {
                throw new IllegalArgumentException("Unknown JSON primitive: " + jsonPrimitive);
            }
        } else if (json instanceof JsonObject object) {
            CompoundBinaryTag.Builder compound = CompoundBinaryTag.builder();

            for (Map.Entry<String, JsonElement> property : object.entrySet()) {
                compound.put(property.getKey(), serialize(property.getValue()));
            }

            return compound.build();
        } else if (json instanceof JsonArray array) {
            List<JsonElement> jsonArray = array.asList();

            if (jsonArray.isEmpty()) {
                return ListBinaryTag.empty();
            }

            List<BinaryTag> tagItems = new ArrayList<>(jsonArray.size());
            BinaryTagType<? extends BinaryTag> listType = null;

            for (JsonElement jsonEl : jsonArray) {
                BinaryTag tag = serialize(jsonEl);
                tagItems.add(tag);

                if (listType == null) {
                    listType = tag.type();
                } else if (listType != tag.type()) {
                    listType = BinaryTagTypes.COMPOUND;
                }
            }

            switch (listType.id()) {
                case 1://BinaryTagTypes.BYTE:
                    byte[] bytes = new byte[jsonArray.size()];
                    for (int i = 0; i < bytes.length; i++) {
                        bytes[i] = jsonArray.get(i).getAsNumber().byteValue();
                    }

                    return ByteArrayBinaryTag.byteArrayBinaryTag(bytes);
                case 3://BinaryTagTypes.INT:
                    int[] ints = new int[jsonArray.size()];
                    for (int i = 0; i < ints.length; i++) {
                        ints[i] = jsonArray.get(i).getAsNumber().intValue();
                    }

                    return IntArrayBinaryTag.intArrayBinaryTag(ints);
                case 4://BinaryTagTypes.LONG:
                    long[] longs = new long[jsonArray.size()];
                    for (int i = 0; i < longs.length; i++) {
                        longs[i] = jsonArray.get(i).getAsNumber().longValue();
                    }

                    return LongArrayBinaryTag.longArrayBinaryTag(longs);
                case 10://BinaryTagTypes.COMPOUND:
                    tagItems.replaceAll(tag -> {
                        if (tag.type() == BinaryTagTypes.COMPOUND) {
                            return tag;
                        } else {
                            return CompoundBinaryTag.builder().put("", tag).build();
                        }
                    });
                    break;
            }

            return ListBinaryTag.listBinaryTag(listType, tagItems);
        }

        return EndBinaryTag.endBinaryTag();
    }

}
