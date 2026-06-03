import i18n from "i18next";
import { initReactI18next } from "react-i18next";

// Dữ liệu nội dung sẽ được nạp thông qua fetch hoặc static import.
// Do file JSON khá nhẹ, ta có thể import trực tiếp.
import viTranslations from "./locales/vi.json";
import enTranslations from "./locales/en.json";

i18n.use(initReactI18next).init({
  resources: {
    vi: {
      translation: viTranslations,
    },
    en: {
      translation: enTranslations,
    },
  },
  lng: "vi", // Ngôn ngữ mặc định
  fallbackLng: "en",
  interpolation: {
    escapeValue: false, // React đã an toàn với XSS rồi
  },
});

export default i18n;
