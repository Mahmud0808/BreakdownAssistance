package com.habibur.breakdown_assistance.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.os.LocaleList;

import com.habibur.breakdown_assistance.R;
import com.habibur.breakdown_assistance.config.Prefs;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LocaleHelper {

    public static Context setLocale(Context context) {
        String localeCode;

        try {
            localeCode = Prefs.getString("app_language", "");
        } catch (Exception e) {
            return context;
        }

        if (localeCode.isEmpty()) {
            LocaleList locales = context.getResources().getConfiguration().getLocales();
            String[] localeCodesArray = context.getResources().getStringArray(R.array.locale_code);
            List<String> localeCodes = Arrays.asList(localeCodesArray);

            for (int i = 0; i < locales.size(); i++) {
                String languageCode = locales.get(i).getLanguage();
                String countryCode = locales.get(i).getCountry();
                String languageFormat = languageCode + "-" + countryCode;

                if (localeCodes.contains(languageFormat)) {
                    localeCode = languageFormat;
                    break;
                }
            }

            if (localeCode.isEmpty()) {
                localeCode = "en-US";
            }
        }

        Locale locale = Locale.forLanguageTag(localeCode);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        LocaleList localeList = new LocaleList(locale);
        LocaleList.setDefault(localeList);
        configuration.setLocales(localeList);

        return context.createConfigurationContext(configuration);
    }
}
