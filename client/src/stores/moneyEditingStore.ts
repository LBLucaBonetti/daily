import { defineStore } from 'pinia';

export const useMoneyInEditStateStore = defineStore('moneyInEditState', {
  state: () => ({
    counter: 0,
  }),
  actions: {
    incrementMoneyInEditState() {
      this.counter++;
    },
    decrementMoneyInEditState() {
      this.counter--;
    },
  },
});
