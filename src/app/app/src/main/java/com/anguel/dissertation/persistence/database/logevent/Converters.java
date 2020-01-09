package com.anguel.dissertation.persistence.database.logevent;

import androidx.room.TypeConverter;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("WeakerAccess")
class Converters {
    @TypeConverter
    public String convertMap(List<Map<String, String>> map) {
        StringBuilder convertedMaps = new StringBuilder();
        AtomicInteger index = new AtomicInteger();

//        for each map that contains app data, convert it into a giant string and save it to the stringbuilder of converted maps
        map.forEach(m -> convertedMaps.append(
                Joiner.on(',').withKeyValueSeparator('=').join(map.get(index.getAndIncrement()))
        ).append('%')); // the % gets appended to symbolize the end of the current list element aka the map

        convertedMaps.trimToSize();
        return convertedMaps.toString();
    }

    @SuppressWarnings("UnstableApiUsage") // withKeyValueSeparator marked as beta since more than 20 version ago...
    @TypeConverter
    public List<Map<String, String>> convertString (String string) {
        List<Map<String, String>> converted = new ArrayList<>();
//        this converts the string into a list of strings by the % sign
        List<String> initialConvertedString = Splitter.on('%').trimResults().splitToList(string);
//        next step is to now turn each string element into a map
        initialConvertedString.forEach(s -> {
            if (s.isEmpty()) { // added for debugging, as there is some empty data added
                converted.add(new HashMap<>());
            } else {
                converted.add(Splitter.on(',').withKeyValueSeparator('=').split(s));
            }
        });

        return converted;
    }
}
