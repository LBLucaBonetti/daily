<template>
  <transition
    appear
    enter-active-class="animated fadeIn"
    leave-active-class="animated fadeOut"
  >
    <q-card
      :class="
        noteEditable
          ? 'q-mt-md bg-1 text-1 border-primary'
          : 'q-mt-md bg-1 text-1'
      "
      flat
      bordered
    >
      <q-card-section class="note-text">
        <q-input
          autogrow
          autofocus
          clearable
          input-class="text-1"
          v-model="noteUpdateText"
          min-height="15rem"
          counter
          maxlength="255"
          borderless
          :rules="[validateNote]"
          lazy-rules="ondemand"
          ref="noteUpdateInput"
          v-if="noteEditable"
        >
        </q-input>
        <q-input
          autogrow
          input-class="text-1"
          v-model="noteText"
          min-height="15rem"
          counter
          maxlength="255"
          borderless
          readonly
          v-else
        ></q-input>
      </q-card-section>

      <q-card-actions align="right">
        <template v-if="noteEditable">
          <q-btn
            flat
            label="Cancel"
            aria-label="Cancel"
            @click="cancelEditNote"
          ></q-btn>
          <q-btn
            flat
            :loading="noteUpdateBtnLoading"
            label="Save"
            aria-label="Save"
            @click="updateNote"
          />
        </template>
        <template v-else>
          <q-btn flat label="Edit" aria-label="Edit" @click="editNote"></q-btn>
          <q-btn
            flat
            :loading="noteDeleteBtnLoading"
            label="Delete"
            @click="askConfirmationToDeleteNote"
            aria-label="Delete"
            color="negative"
          ></q-btn>
        </template>
      </q-card-actions>
    </q-card>
  </transition>
</template>

<script setup lang="ts">
import { validateNote } from 'src/validators/note-validator';
import { AxiosResponse } from 'axios';
import {
  QCard,
  QCardSection,
  QCardActions,
  QBtn,
  useQuasar,
  QInput,
} from 'quasar';
import { api } from 'src/boot/axios';
import NoteDto from 'src/interfaces/NoteDto';
import { onMounted, PropType, ref } from 'vue';
import { refreshPage } from 'src/utils/refresh-page';
import { isAxios401 } from 'src/utils/is-axios-401';
import { notifyPosition } from 'src/utils/notify-position';
import { useNotesInEditStateStore } from 'src/stores/noteEditingStore';

const $q = useQuasar();
const noteText = ref('');
const noteDeleteBtnLoading = ref(false);
const noteEditable = ref(false);
const noteUpdateText = ref('');
const noteUpdateBtnLoading = ref(false);
const noteUpdateInput = ref<QInput | null>(null);
const notesInEditStateCounter = useNotesInEditStateStore();

const props = defineProps({
  note: { type: Object as PropType<NoteDto>, required: true },
});

const emit = defineEmits(['reloadNotes']);

onMounted(() => {
  noteText.value = props.note.text;
});

async function updateNote() {
  // Validate first
  if (!noteUpdateInput.value || noteUpdateInput.value.validate() !== true)
    return;
  // Set loading and do stuff
  noteUpdateBtnLoading.value = true;
  try {
    const updateNoteDto: NoteDto = {
      text: noteUpdateText.value,
      uuid: props.note.uuid,
    };
    const res: AxiosResponse<NoteDto> = await api.put(
      '/notes/' + updateNoteDto.uuid,
      updateNoteDto
    );
    if (res.status === 204) {
      $q.notify({
        classes: 'q-px-lg',
        position: notifyPosition($q),
        progress: true,
        message: 'Note correctly saved',
        color: 'white',
        textColor: 'info',
        icon: 'img:icons/success.svg',
        iconColor: 'primary',
        iconSize: '20px',
      });
      // Restore non-editable state
      noteText.value = noteUpdateText.value;
      notesInEditStateCounter.decrementNotesInEditState();
      noteEditable.value = false;
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
      message: 'Error saving note',
      color: 'white',
      textColor: 'info',
      icon: 'img:icons/error.svg',
      iconColor: 'primary',
      iconSize: '20px',
    });
  } finally {
    noteUpdateBtnLoading.value = false;
  }
}

function editNote() {
  noteUpdateText.value = noteText.value;
  notesInEditStateCounter.incrementNotesInEditState();
  noteEditable.value = true;
}

function cancelEditNote() {
  notesInEditStateCounter.decrementNotesInEditState();
  noteEditable.value = false;
}

function askConfirmationToDeleteNote() {
  $q.dialog({
    title: 'Confirm',
    message: 'Do you really want to delete the note?',
    persistent: true,
    class: 'bg-1 text-1',
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
