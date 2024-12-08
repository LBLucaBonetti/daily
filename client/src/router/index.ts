import { defineRouter } from '#q-app/wrappers';
import {
  createMemoryHistory,
  createRouter,
  createWebHashHistory,
  createWebHistory,
} from 'vue-router';

import routes from './routes';

/*
 * If not building with SSR mode, you can
 * directly export the Router instantiation;
 *
 * The function below can be async too; either use
 * async/await or return a Promise which resolves
 * with the Router instance.
 *
 * The function defined takes { store, ssrContext } as optional params
 */

export default defineRouter(function () {
  let createHistory;
  if (process.env.SERVER) createHistory = createMemoryHistory;
  else {
    if (process.env.VUE_ROUTER_MODE === 'history') {
      createHistory = createWebHistory;
    } else {
      createHistory = createWebHashHistory;
    }
  }

  const Router = createRouter({
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,

    // Leave this as is and make changes in quasar.conf.js instead!
    // quasar.conf.js -> build -> vueRouterMode
    // quasar.conf.js -> build -> publicPath
    history: createHistory(process.env.VUE_ROUTER_BASE),
  });

  return Router;
});
