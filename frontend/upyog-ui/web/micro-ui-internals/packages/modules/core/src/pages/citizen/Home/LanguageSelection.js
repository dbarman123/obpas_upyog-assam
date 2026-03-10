import React, { useMemo, useEffect } from "react";
import { PageBasedInput, Loader, RadioButtons, CardHeader } from "@upyog/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";

const LanguageSelection = () => {
  const { t } = useTranslation();
  const history = useHistory();

  const { data: { languages, stateInfo } = {}, isLoading } = Digit.Hooks.useStore.getInitData();
  const selectedLanguage = Digit.StoreData.getCurrentLanguage();

  useEffect(() => {
    if (languages && !selectedLanguage) {
      const englishLanguage = languages.find(lang => lang.value === 'en_IN' || lang.label.toLowerCase().includes('english'));
      if (englishLanguage) {
        Digit.LocalizationService.changeLanguage(englishLanguage.value, stateInfo?.code);
        setTimeout(() => {
          history.push(`/upyog-ui/citizen/login-selection`);
        }, 100);
      }
    }
  }, [languages, selectedLanguage, stateInfo, history]);
  const texts = useMemo(
    () => ({
      header: t("CS_COMMON_CHOOSE_LANGUAGE"),
      submitBarLabel: t("CORE_COMMON_CONTINUE"),
    }),
    [t]
  );

  const RadioButtonProps = useMemo(
    () => ({
      options: languages,
      optionsKey: "label",
      additionalWrapperClass: "reverse-radio-selection-wrapper",
      onSelect: (language) => Digit.LocalizationService.changeLanguage(language.value, stateInfo.code),
      selectedOption: languages?.filter((i) => i.value === selectedLanguage)[0],
    }),
    [selectedLanguage, languages]
  );
  if(selectedLanguage==="en_IN"){
    history.push(`/upyog-ui/citizen/login-selection`);
  }

  function onSubmit() {
    history.push(`/upyog-ui/citizen/login-selection`);
  }

  return isLoading ? (
    <Loader />
  ) : (
    <div className="selection-card-wrapper">
      <PageBasedInput texts={texts} onSubmit={onSubmit}>
        <CardHeader>{t("CS_COMMON_CHOOSE_LANGUAGE")}</CardHeader>
        <RadioButtons {...RadioButtonProps} />
      </PageBasedInput>
    </div>
  );
};

export default LanguageSelection;
