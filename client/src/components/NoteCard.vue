<template>
  <transition
    appear
    enter-active-class="animated fadeIn"
    leave-active-class="animated fadeOut"
  >
    <q-card class="q-mt-md bg-1 text-1" flat bordered>
      <q-card-section class="break-all">
        {{ note.text }}
      </q-card-section>

      <q-card-actions align="right">
        <q-btn flat label="Edit" @click="updateNote"></q-btn>
        <q-btn
          flat
          :loading="noteDeleteBtnLoading"
          label="Delete"
          @click="askConfirmationToDeleteNote"
          aria-label="Delete"
          color="negative"
        ></q-btn>
      </q-card-actions>
    </q-card>
  </transition>
</template>

<script setup lang="ts">
import { AxiosResponse } from 'axios';
import { QCard, QCardSection, QCardActions, QBtn, useQuasar } from 'quasar';
import { api } from 'src/boot/axios';
import NoteDto from 'src/interfaces/NoteDto';
import { PropType, ref } from 'vue';
import { refreshPage } from 'src/utils/refresh-page';
import { isAxios401 } from 'src/utils/is-axios-401';
import TheNoteUpdateDialog from './TheNoteUpdateDialog.vue';
import { notifyPosition } from 'src/utils/notify-position';

const $q = useQuasar();
const noteDeleteBtnLoading = ref(false);

const props = defineProps({
  note: { type: Object as PropType<NoteDto>, required: true },
});

const emit = defineEmits(['reloadNotes']);

function updateNote() {
  $q.dialog({
    component: TheNoteUpdateDialog,
    componentProps: { note: props.note },
  }).onOk(() => {
    //Reload notes
    emit('reloadNotes');
  });
}

function askConfirmationToDeleteNote() {
  $q.dialog({
    title: 'Confirm',
    message: 'Do you really want to delete the note?',
    persistent: true,
    ok: {
      flat: true,
      color: 'negative',
    },
    cancel: {
      flat: true,
      class: 'bg-1 text-1',
    },
    focus: 'cancel',
  }).onOk(() => {
    deleteNote();
  });
}

async function deleteNote() {
  noteDeleteBtnLoading.value = true;
  try {
    const res: AxiosResponse<NoteDto> = await api.delete(
      '/notes/' + props.note.uuid
    );
    if (res.status === 204) {
      $q.notify({
        classes: 'q-px-lg',
        position: notifyPosition($q),
        progress: true,
        message: 'Note correctly deleted',
        color: 'white',
        textColor: 'info',
        icon: 'img:icons/success.svg',
        iconColor: 'primary',
        iconSize: '20px',
      });
      // Reload notes
      emit('reloadNotes');
    }
  } catch (err) {
    if (isAxios401(err)) {
      refreshPage();
      return;
    }
    $q.notify({
      classes: 'q-px-lg',
      position: notifyPosition($q),
      progress: true,
      message: 'Error deleting note',
      color: 'white',
      textColor: 'info',
      icon: 'img:icons/error.svg',
      iconColor: 'primary',
      iconSize: '20px',
    });
  } finally {
    noteDeleteBtnLoading.value = false;
  }
}
</script>
