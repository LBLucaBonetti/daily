import { refreshPage } from 'src/utils/refresh-page';
import { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: { name: 'notes' },
    component: () =>
      import('layouts/MainLayout.vue')
        .then((mainLayout) => mainLayout)
        .catch(refreshPage),
    children: [
      {
        name: 'notes',
        path: 'notes',
        component: () =>
          import('src/pages/NotesPage.vue')
            .then((notesPage) => notesPage)
            .catch(refreshPage),
      },
      {
        name: 'tags',
        path: 'tags',
        component: () =>
          import('src/pages/TagsPage.vue')
            .then((tagsPage) => tagsPage)
            .catch(refreshPage),
      },
      {
        name: 'settings',
        path: 'settings',
        component: () =>
          import('src/pages/SettingsPage.vue')
            .then((settingsPage) => settingsPage)
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
