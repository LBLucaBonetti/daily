import { refreshPage } from 'src/utils/refresh-page';
import { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () =>
      import('layouts/MainLayout.vue')
        .then((mainLayout) => mainLayout)
        .catch(refreshPage),
    children: [
      {
        path: '',
        component: () =>
          import('pages/IndexPage.vue')
            .then((indexPage) => indexPage)
            .catch(refreshPage),
      },
    ],
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () =>
      import('pages/ErrorNotFound.vue')
        .then((errorNotFound) => errorNotFound)
        .catch(refreshPage),
  },
];

export default routes;
