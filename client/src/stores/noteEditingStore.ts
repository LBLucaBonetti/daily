import { defineStore } from 'pinia';

export const useNotesInEditStateStore = defineStore('notesInEditState', {
  state: () => ({
    counter: 0,
  }),
  actions: {
    incrementNotesInEditState() {
      this.counter++;
    },
    decrementNotesInEditState() {
      this.counter--;
    },
  },
});
