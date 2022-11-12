import { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () =>
      import('layouts/MainLayout.vue')
        .then((mainLayout) => mainLayout)
        .catch(() => (window.location.href = window.location.href)),
    children: [
      {
        path: '',
        component: () =>
          import('pages/IndexPage.vue')
            .then((indexPage) => indexPage)
            .catch(() => (window.location.href = window.location.href)),
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
        .catch(() => (window.location.href = window.location.href)),
  },
];

export default routes;
