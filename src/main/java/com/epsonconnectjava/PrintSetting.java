package com.epsonconnectjava;

import java.util.*;

public class PrintSetting {

    private static final Set<String> VALID_PRINT_MODES;
    private static final Set<String> VALID_MEDIA_SIZES;
    private static final Set<String> VALID_MEDIA_TYPES;
    private static final Set<String> VALID_PRINT_QUALITIES;
    private static final Set<String> VALID_PAPER_SOURCES;
    private static final Set<String> VALID_COLOR_MODES;
    private static final Set<String> VALID_TWO_SIDE;

    static {
        VALID_PRINT_MODES = new HashSet<>();
        VALID_PRINT_MODES.add("document");
        VALID_PRINT_MODES.add("photo");

        VALID_MEDIA_SIZES = new HashSet<>();
        VALID_MEDIA_SIZES.add("ms_a3");
        VALID_MEDIA_SIZES.add("ms_a4");
        VALID_MEDIA_SIZES.add("ms_a5");
        VALID_MEDIA_SIZES.add("ms_a6");
        VALID_MEDIA_SIZES.add("ms_b5");
        VALID_MEDIA_SIZES.add("ms_tabloid");
        VALID_MEDIA_SIZES.add("ms_letter");
        VALID_MEDIA_SIZES.add("ms_legal");
        VALID_MEDIA_SIZES.add("ms_halfletter");
        VALID_MEDIA_SIZES.add("ms_kg");
        VALID_MEDIA_SIZES.add("ms_l");
        VALID_MEDIA_SIZES.add("ms_2l");
        VALID_MEDIA_SIZES.add("ms_10x12");
        VALID_MEDIA_SIZES.add("ms_8x10");
        VALID_MEDIA_SIZES.add("ms_hivision");
        VALID_MEDIA_SIZES.add("ms_5x8");
        VALID_MEDIA_SIZES.add("ms_postcard");

        VALID_MEDIA_TYPES = new HashSet<>();
        VALID_MEDIA_TYPES.add("mt_plainpaper");
        VALID_MEDIA_TYPES.add("mt_photopaper");
        VALID_MEDIA_TYPES.add("mt_hagaki");
        VALID_MEDIA_TYPES.add("mt_hagakiphoto");
        VALID_MEDIA_TYPES.add("mt_hagakiinkjet");

        VALID_PRINT_QUALITIES = new HashSet<>();
        VALID_PRINT_QUALITIES.add("high");
        VALID_PRINT_QUALITIES.add("normal");
        VALID_PRINT_QUALITIES.add("draft");

        VALID_PAPER_SOURCES = new HashSet<>();
        VALID_PAPER_SOURCES.add("auto");
        VALID_PAPER_SOURCES.add("rear");
        VALID_PAPER_SOURCES.add("front1");
        VALID_PAPER_SOURCES.add("front2");
        VALID_PAPER_SOURCES.add("front3");
        VALID_PAPER_SOURCES.add("front4");

        VALID_COLOR_MODES = new HashSet<>();
        VALID_COLOR_MODES.add("color");
        VALID_COLOR_MODES.add("mono");

        VALID_TWO_SIDE = new HashSet<>();
        VALID_COLOR_MODES.add("none");
        VALID_COLOR_MODES.add("long");
        VALID_COLOR_MODES.add("short");
    }

    /**
     * Merge the given settings with default printer settings.
     * <p>
     * This function will set defaults for any missing settings from the input. It also
     * generates a random job name if one is not provided.
     *
     * @param settings A map containing printer settings.
     * @return A map containing the merged settings.
     */
    public static Map<String, Object> mergeWithDefaultSettings(Map<String, Object> settings) {
        if (settings == null) {
            settings = new HashMap<>();
        }

        String jobName = (String) settings.getOrDefault("job_name", "");
        if (jobName.isEmpty()) {
            // Generate random name if one is not given.
            jobName = "job-" + generateRandomString(8);
        }
        settings.put("job_name", jobName);
        settings.put("print_mode", settings.getOrDefault("print_mode", "document"));

        Map<String, Object> printSetting = (Map<String, Object>) settings.getOrDefault("print_setting", new HashMap<>());

        if (printSetting.isEmpty()) {
            return settings;
        }

        Boolean collate = (Boolean) printSetting.get("collate");
        if (collate == null) {
            collate = true;
        }

        Map<String, Object> mergedPrintSetting = new HashMap<>();
        mergedPrintSetting.put("media_size", printSetting.getOrDefault("media_size", "ms_a4"));
        mergedPrintSetting.put("media_type", printSetting.getOrDefault("media_type", "mt_plainpaper"));
        mergedPrintSetting.put("borderless", printSetting.getOrDefault("borderless", false));
        mergedPrintSetting.put("print_quality", printSetting.getOrDefault("print_quality", "normal"));
        mergedPrintSetting.put("source", printSetting.getOrDefault("source", "auto"));
        mergedPrintSetting.put("color_mode", printSetting.getOrDefault("color_mode", "color"));
        mergedPrintSetting.put("2_sided", printSetting.getOrDefault("2_sided", "none"));
        mergedPrintSetting.put("reverse_order", printSetting.getOrDefault("reverse_order", false));
        mergedPrintSetting.put("copies", printSetting.getOrDefault("copies", 1));
        mergedPrintSetting.put("collate", collate);

        settings.put("print_setting", mergedPrintSetting);
        return settings;
    }
    /**
     * Generates a random string of the specified length using uppercase and lowercase alphabets.
     *
     * @param length The desired length of the random string.
     * @return A random string of the specified length.
     */
    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }

        return result.toString();
    }

    /**
     * Validates the provided settings map. The method performs several checks on the map keys
     * and values to ensure they adhere to predefined criteria.
     *
     * Valid keys for the settings map include "job_name", "print_mode", and "print_setting".
     * The method also validates sub-settings within the "print_setting" key if present.
     *
     * @param settings The map containing the settings to be validated.
     * @throws IllegalArgumentException If the settings map contains invalid keys or if the values do not adhere to predefined criteria.
     * @throws PrintSettingError If the sub-settings within the "print_setting" key do not adhere to predefined criteria.
     */
    public static void validateSettings(Map<String, Object> settings) {
        // Start with the basic checks
        Set<String> extraKeys = new HashSet<>(settings.keySet());
        extraKeys.removeAll(Arrays.asList("job_name", "print_mode", "print_setting"));
        if (!extraKeys.isEmpty()) {
            throw new IllegalArgumentException("Invalid settings keys " + extraKeys);
        }

        String jobName = (String) settings.get("job_name");
        if (jobName.length() > 256) {
            throw new IllegalArgumentException("Job name is greater than 256 chars: " + jobName);
        }

        String printMode = (String) settings.get("print_mode");
        if (!VALID_PRINT_MODES.contains(printMode)) {
            throw new IllegalArgumentException("Invalid print mode " + printMode);
        }

        // Continue with other validations for print_setting if present
        Map<String, Object> printSetting = (Map<String, Object>) settings.get("print_setting");
        if (printSetting != null) {
            String mediaSize = (String) printSetting.get("media_size");
            if (!VALID_MEDIA_SIZES.contains(mediaSize)) {
                throw new IllegalArgumentException("Invalid paper size " + mediaSize);
            }

        } else {
            return;
        }
        // media_type
        String mediaType = (String) printSetting.get("media_type");
        if (!VALID_MEDIA_TYPES.contains(mediaType)) {
            throw new PrintSettingError("Invalid media type " + mediaType);
        }

        // borderless
        Object borderlessObj = printSetting.get("borderless");
        if (!(borderlessObj instanceof Boolean)) {
            throw new PrintSettingError("borderless must be a bool");
        }

        // print_quality
        String printQuality = (String) printSetting.get("print_quality");
        if (!VALID_PRINT_QUALITIES.contains(printQuality)) {
            throw new PrintSettingError("Invalid print quality " + printQuality);
        }

        // Paper source
        String source = (String) printSetting.get("source");
        if (!VALID_PAPER_SOURCES.contains(source)) {
            throw new PrintSettingError("Invalid source " + source);
        }

        // color_mode
        String colorMode = (String) printSetting.get("color_mode");
        if (!VALID_COLOR_MODES.contains(colorMode)) {
            throw new PrintSettingError("Invalid color mode " + colorMode);
        }

        // two_sided
        String twoSided = (String) printSetting.get("2_sided");
        if (!VALID_TWO_SIDE.contains(twoSided)) {
            throw new PrintSettingError("Invalid 2-sided value " + twoSided);
        }

        // reverse_order
        Object reverseOrderObj = printSetting.get("reverse_order");
        if (!(reverseOrderObj instanceof Boolean)) {
            throw new PrintSettingError("Reverse order must be a bool");
        }
        boolean reverseOrder = (Boolean) reverseOrderObj;

        if (("long".equals(twoSided) || "short".equals(twoSided)) && reverseOrder) {
            throw new PrintSettingError("Cannot use reverse order when using two-sided printing.");
        }

        // copies
        int copies = (Integer) printSetting.get("copies");
        if (copies < 1 || copies > 99) {
            throw new PrintSettingError("Invalid number of copies " + copies);
        }

        // collate
        Object collateObj = printSetting.get("collate");
        if (!(collateObj instanceof Boolean)) {
            throw new PrintSettingError("Collate must be a bool");
        }
        boolean collate = (Boolean) collateObj;

        if (("long".equals(twoSided) || "short".equals(twoSided)) && !collate) {
            throw new PrintSettingError("Must collate when using two-sided printing.");
        }
    }
}

/**
 * Custom exception class to handle errors related to print settings.
 */
class PrintSettingError extends RuntimeException {
    public PrintSettingError(String message) {
        super(message);
    }
}