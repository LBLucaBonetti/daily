import { defineStore } from 'pinia';

export const useLanguageStore = defineStore('language', {
  state: () => ({
    language: 'en-US',
  }),

  actions: {
    setLanguage(newLanguage: string) {
      this.language = newLanguage;
    },
  },
});
