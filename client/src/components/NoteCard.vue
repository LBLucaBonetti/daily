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
          :rules="[
            (val) => validateNote(val, $t('note.save.validation.empty')),
          ]"
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

      <q-card-section>
        <q-select
          borderless
          dense
          v-model="addTagToNoteSelectModel"
          use-input
          :multiple="false"
          hide-dropdown-icon
          hide-selected
          input-debounce="500"
          input-class="text-1"
          :options="addTagToNoteSelectOptions"
          option-label="name"
          @filter="addTagToNoteSelectFilter"
          @update:model-value="addTagToNote"
          :label="$t('tag.addToNote.placeholder')"
        >
          <template v-slot:no-option>
            <q-item>
              <q-item-section class="text-1">{{
                $t('tag.addToNote.noResults')
              }}</q-item-section>
            </q-item>
          </template>
        </q-select>
        <div class="row q-mt-md" style="gap: 24px">
          <tag-chip
            v-for="tag in tags"
            :key="tag.uuid"
            :tag="tag"
            @remove="removeTagFromNote(tag)"
          ></tag-chip>
        </div>
      </q-card-section>

      <q-card-section>
        <q-card-actions class="no-padding" :vertical="$q.screen.lt.sm">
          <div :class="$q.screen.lt.sm ? 'text-3 text-center' : 'text-3'">
            {{
              $t('note.created') +
              ' ' +
              new Date(note.createdAt?.toString() + 'Z')?.toLocaleString(
                languageStore.language,
              )
            }}
          </div>
          <q-space></q-space>
          <template v-if="noteEditable">
            <q-btn
              flat
              :label="$t('dialog.cancel')"
              aria-label="Cancel"
              @click="cancelEditNote"
            ></q-btn>
            <q-btn
              unelevated
              :loading="noteUpdateBtnLoading"
              :disable="noteText === noteUpdateText"
              :label="$t('dialog.save')"
              aria-label="Save"
              @click="updateNote"
              color="primary"
            />
          </template>
          <template v-else>
            <q-btn
              flat
              :label="$t('dialog.edit')"
              aria-label="Edit"
              @click="editNote"
            ></q-btn>
            <q-btn
              unelevated
              :loading="noteDeleteBtnLoading"
              :label="$t('dialog.delete')"
              @click="askConfirmationToDeleteNote"
              aria-label="Delete"
              color="negative"
            ></q-btn>
          </template>
        </q-card-actions>
      </q-card-section>
    </q-card>
  </transition>
</template>

<script setup lang="ts">
import { validateNote } from 'src/validators/note-validator';
import type { AxiosError, AxiosResponse } from 'axios';
import {
  QCard,
  QCardSection,
  QCardActions,
  QBtn,
  useQuasar,
  QInput,
} from 'quasar';
import { api } from 'src/boot/axios';
import type NoteDto from 'src/interfaces/NoteDto';
import { onMounted, type PropType, ref } from 'vue';
import { refreshPage } from 'src/utils/refresh-page';
import { isAxios401 } from 'src/utils/is-axios-401';
import { notifyPosition } from 'src/utils/notify-position';
import { useNotesInEditStateStore } from 'src/stores/noteEditingStore';
import { useI18n } from 'vue-i18n';
import { useLanguageStore } from 'src/stores/languageStore';
import type TagDto from 'src/interfaces/TagDto';
import TagChip from '../components/TagChip.vue';
import type PageDto from 'src/interfaces/PageDto';

const { t } = useI18n();
const $q = useQuasar();
const noteText = ref('');
const noteDeleteBtnLoading = ref(false);
const noteEditable = ref(false);
const noteUpdateText = ref('');
const noteUpdateBtnLoading = ref(false);
const noteUpdateInput = ref<QInput | null>(null);
const notesInEditStateCounter = useNotesInEditStateStore();
const languageStore = useLanguageStore();
const tags = ref<TagDto[]>([]);
const addTagToNoteSelectModel = ref<TagDto | null>(null);
const addTagToNoteSelectOptions = ref<TagDto[]>([]);

const props = defineProps({
  note: { type: Object as PropType<NoteDto>, required: true },
});

const emit = defineEmits(['reloadNotes']);

onMounted(() => {
  noteText.value = props.note.text;
  loadTags();
});

function reloadTags() {
  loadTags();
}

function loadTags() {
  api
    .get('/notes/' + props.note.uuid + '/tags')
    .then((res: AxiosResponse<TagDto[]>) => {
      tags.value = [...res.data];
    })
    .catch((err: Error | AxiosError) => {
      if (isAxios401(err)) {
        refreshPage();
        return;
      }
      console.error(
        'Could not retrieve tags for note with id ' + props.note.uuid,
      );
    });
}

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
      updateNoteDto,
    );
    if (res.status === 200) {
      $q.notify({
        classes: 'q-px-lg',
        position: notifyPosition($q),
        progress: true,
        message: t('note.save.ok'),
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
      message: t('note.save.error'),
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
    title: t('dialog.confirm'),
    message: t('note.delete.confirm'),
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
      '/notes/' + props.note.uuid,
    );
    if (res.status === 204) {
      $q.notify({
        classes: 'q-px-lg',
        position: notifyPosition($q),
        progress: true,
        message: t('note.delete.ok'),
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
      message: t('note.delete.error'),
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

function addTagToNoteSelectFilter(
  userProvidedName: string,
  update: (doneFn: () => void) => void,
) {
  update(() => {
    if (userProvidedName === '') return;
    // Search tags
    api
      .get('/tags', { params: { name: userProvidedName } })
      .then((res: AxiosResponse<PageDto<TagDto>>) => {
        // Replace the content of the options each time
        addTagToNoteSelectOptions.value = [...res.data.content];
      })
      .catch((err: Error | AxiosError) => {
        if (isAxios401(err)) {
          refreshPage();
          return;
        }
        console.error('Could not search tags');
      });
  });
}

async function addTagToNote(value: TagDto) {
  try {
    if (!value) {
      return;
    }

    // Add tag to note, if it's already present for the current note, it should not be a problem
    const res: AxiosResponse<TagDto> = await api.put(
      '/notes/' + props.note.uuid + '/tags/' + value.uuid,
    );
    if (res.status === 204) {
      $q.notify({
        classes: 'q-px-lg',
        position: notifyPosition($q),
        progress: true,
        message: t('tag.addToNote.ok'),
        color: 'white',
        textColor: 'info',
        icon: 'img:icons/success.svg',
        iconColor: 'primary',
        iconSize: '20px',
      });
      reloadTags();
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
      message: t('tag.addToNote.error'),
      color: 'white',
      textColor: 'info',
      icon: 'img:icons/error.svg',
      iconColor: 'primary',
      iconSize: '20px',
    });
  } finally {
    // Reset select options anyway
    addTagToNoteSelectModel.value = null;
    addTagToNoteSelectOptions.value = [];
  }
}

async function removeTagFromNote(tag: TagDto) {
  try {
    // Remove tag from note
    const res: AxiosResponse<TagDto> = await api.delete(
      '/notes/' + props.note.uuid + '/tags/' + tag.uuid,
    );
    if (res.status === 204) {
      $q.notify({
        classes: 'q-px-lg',
        position: notifyPosition($q),
        progress: true,
        message: t('tag.removeFromNote.ok'),
        color: 'white',
        textColor: 'info',
        icon: 'img:icons/success.svg',
        iconColor: 'primary',
        iconSize: '20px',
      });
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
      message: t('tag.removeFromNote.error'),
      color: 'white',
      textColor: 'info',
      icon: 'img:icons/error.svg',
      iconColor: 'primary',
      iconSize: '20px',
    });
  } finally {
    // Reload tags anyway
    reloadTags();
  }
}
</script>
