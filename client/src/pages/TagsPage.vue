<template>
  <page-title
    :translatedTitle="$t('pages.tags.title')"
    :translatedSubtitle="$t('pages.tags.subtitle')"
  ></page-title>

  <q-input
    outlined
    autogrow
    autofocus
    clearable
    input-class="text-1"
    v-model="tagName"
    :placeholder="$t('tag.name.placeholder')"
    counter
    maxlength="30"
    :rules="[(val) => validateTag(val, $t('tag.save.validation.name.empty'))]"
    lazy-rules="ondemand"
    ref="tagNameInput"
  >
  </q-input>
  <q-input
    outlined
    autogrow
    clearable
    inputClass="text-1"
    v-model="tagColorHex"
    :rules="['hexColor']"
    :hint="$t('tag.colorHex.placeholder')"
    maxlength="7"
    class="q-mt-md"
  >
    <template v-slot:append>
      <q-icon name="colorize" class="cursor-pointer">
        <q-popup-proxy cover transition-show="scale" transition-hide="scale">
          <q-color v-model="tagColorHex" :defaultValue="primaryColor" />
        </q-popup-proxy>
      </q-icon>
    </template>
  </q-input>

  <div class="row justify-end">
    <q-btn
      unelevated
      padding="sm xl"
      :aria-label="$t('tag.save.button')"
      class="q-mt-sm bg-2 text-2"
      @click="saveTag"
      :label="$t('tag.save.button')"
      ref="tagSaveBtn"
      :loading="tagSaveBtnLoading"
    ></q-btn>
  </div>

  <q-infinite-scroll @load="onLoad" ref="infiniteScroll">
    <div class="row q-mt-md justify-center" style="gap: 24px">
      <div v-for="tag in tags" :key="tag.uuid">
        <tag-chip
          :tag="tag"
          @touch="openEditTagDialog(tag)"
          @remove="askConfirmationToRemoveTag(tag)"
        ></tag-chip>
      </div>
    </div>

    <template v-slot:loading>
      <div :class="loadingClass">
        <q-spinner-oval style="font-size: 72px"></q-spinner-oval>
      </div>
    </template>
  </q-infinite-scroll>

  <!-- Edit tag dialog -->
  <q-dialog
    v-model="editTagDialog"
    noBackdropDismiss
    noEscDismiss
    @show="onShowEditTagDialog"
    ><q-card class="bg-1 text-1"
      ><q-card-section
        ><div class="text-h6 text-1">
          {{ $t('tag.update.dialog.title') }}
        </div></q-card-section
      >

      <q-card-section class="q-pt-none">
        <q-input
          outlined
          autogrow
          autofocus
          clearable
          input-class="text-1"
          v-model="editTagName"
          :placeholder="$t('tag.name.placeholder')"
          counter
          maxlength="30"
          :rules="[
            (val) => validateTag(val, $t('tag.save.validation.name.empty')),
          ]"
          lazy-rules="ondemand"
          ref="editTagNameInput"
          class="tag-name-input"
        >
        </q-input
        ><q-input
          outlined
          autogrow
          clearable
          inputClass="text-1"
          v-model="editTagColorHex"
          :rules="['hexColor']"
          :hint="$t('tag.colorHex.placeholder')"
          maxlength="7"
          class="q-mt-md"
          ><template v-slot:append>
            <q-icon name="colorize" class="cursor-pointer">
              <q-popup-proxy
                cover
                transition-show="scale"
                transition-hide="scale"
              >
                <q-color v-model="editTagColorHex" />
              </q-popup-proxy>
            </q-icon> </template></q-input
      ></q-card-section>

      <q-card-actions align="right">
        <q-btn
          class="bg-1 text-1"
          flat
          :label="$t('tag.update.dialog.cancel')"
          color="primary"
          @click="cancelEditTag"
          ref="cancelEditTagBtn"
        />
        <q-btn
          class="bg-1 text-1"
          flat
          :label="$t('tag.update.dialog.confirm')"
          color="primary"
          @click="askConfirmationToEditTag"
        /> </q-card-actions></q-card
  ></q-dialog>
</template>
<script setup lang="ts">
import PageTitle from 'src/components/PageTitle.vue';
import { api } from 'src/boot/axios';
import { AxiosError, AxiosResponse } from 'axios';
import PageDto from 'src/interfaces/PageDto';
import TagDto from 'src/interfaces/TagDto';
import { isAxios401 } from 'src/utils/is-axios-401';
import { notifyPosition } from 'src/utils/notify-position';
import { refreshPage } from 'src/utils/refresh-page';
import { ref, computed } from 'vue';
import { QBtn, QInfiniteScroll, QInput, useQuasar, QDialog } from 'quasar';
import { useI18n } from 'vue-i18n';
import TagChip from 'src/components/TagChip.vue';
import { validateTag } from 'src/validators/tag-validator';

const tags = ref<TagDto[]>([]);
const infiniteScroll = ref<QInfiniteScroll | null>(null);
const $q = useQuasar();
const { t } = useI18n();
const tagName = ref('');
const primaryColor = '#ea580c'; // Taken from the color palette
const tagColorHex = ref(primaryColor);
const editTagName = ref('');
const editTagColorHex = ref(primaryColor);
const editTagUuid = ref('');
const tagNameInput = ref<QInput | null>(null);
const editTagNameInput = ref<QInput | null>(null);
const tagSaveBtnLoading = ref(false);
const tagSaveBtn = ref<QBtn | null>(null);
const loadingClass = computed(() => {
  const alwaysPresentClasses = 'row justify-center ';
  return tags.value && tags.value.length > 0
    ? alwaysPresentClasses + 'q-mt-md'
    : alwaysPresentClasses;
});
const editTagDialog = ref(false);
const cancelEditTagBtn = ref<QBtn | null>(null);

async function saveTag() {
  // Validate first
  if (!tagNameInput.value || tagNameInput.value.validate() !== true) return;
  if (
    !tagColorHex.value ||
    validateTag(tagColorHex.value, t('tag.save.validation.colorHex.empty')) !==
      true
  )
    return;
  // Set loading and do stuff
  tagSaveBtnLoading.value = true;
  try {
    const tagDto: TagDto = {
      uuid: '',
      name: tagName.value,
      colorHex: tagColorHex.value,
    };
    const res: AxiosResponse<TagDto> = await api.post('/tags', tagDto);
    if (res.status === 201) {
      $q.notify({
        classes: 'q-px-lg',
        position: notifyPosition($q),
        progress: true,
        message: t('tag.save.ok'),
        color: 'white',
        textColor: 'info',
        icon: 'img:icons/success.svg',
        iconColor: 'primary',
        iconSize: '20px',
      });
      // Reload tags
      reloadTags();
      // Reset tag text and colorHex
      tagName.value = '';
      tagColorHex.value = primaryColor;
      // Set focus back to input
      tagNameInput.value.focus();
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
      message: t('tag.save.error'),
      color: 'white',
      textColor: 'info',
      icon: 'img:icons/error.svg',
      iconColor: 'primary',
      iconSize: '20px',
    });
  } finally {
    tagSaveBtnLoading.value = false;
  }
}

function onLoad(index: number, done: () => void) {
  api
    .get('/tags', {
      params: {
        sort: 'createdAt,DESC',
        size: 100,
        page: index - 1,
      },
    })
    .then((res: AxiosResponse<PageDto<TagDto>>) => {
      tags.value.push(...res.data.content);
      done();
      if (res.data.last && infiniteScroll.value !== null) {
        // Stop infinite scroll
        infiniteScroll.value.stop();
      }
    })
    .catch((err: Error | AxiosError) => {
      if (isAxios401(err)) {
        refreshPage();
        return;
      }
      $q.notify({
        classes: 'q-px-lg',
        position: notifyPosition($q),
        progress: true,
        message: t('tag.loading.error'),
        color: 'white',
        textColor: 'info',
        icon: 'img:icons/error.svg',
        iconColor: 'primary',
        iconSize: '20px',
      });
    });
}

function reloadTags() {
  if (infiniteScroll.value === null) {
    return;
  }

  infiniteScroll.value.reset();
  tags.value = [];
  infiniteScroll.value.resume();
  infiniteScroll.value.trigger();
}

function openEditTagDialog(tag: TagDto) {
  // Set edit tag dialog fields
  editTagName.value = tag.name;
  editTagColorHex.value = tag.colorHex;
  editTagUuid.value = tag.uuid;
  editTagDialog.value = true;
}

function cancelEditTag() {
  // Reset edit tag dialog fields
  editTagName.value = '';
  editTagColorHex.value = primaryColor;
  editTagDialog.value = false;
}

function askConfirmationToEditTag() {
  $q.dialog({
    title: t('dialog.confirm'),
    message: t('tag.update.confirm'),
    persistent: true,
    class: 'bg-1 text-1',
    ok: {
      flat: true,
      class: 'bg-1 text-1',
    },
    cancel: {
      flat: true,
      class: 'bg-1 text-1',
    },
    focus: 'cancel',
  }).onOk(() => {
    updateTag();
  });
}

async function updateTag() {
  try {
    const tagDto: TagDto = {
      name: editTagName.value,
      colorHex: editTagColorHex.value,
      uuid: editTagUuid.value,
    };
    const res: AxiosResponse<TagDto> = await api.put(
      '/tags/' + editTagUuid.value,
      tagDto,
    );
    if (res.status === 200) {
      // Close dialog
      editTagDialog.value = false;
      // Show ok message
      $q.notify({
        classes: 'q-px-lg',
        position: notifyPosition($q),
        progress: true,
        message: t('tag.update.ok'),
        color: 'white',
        textColor: 'info',
        icon: 'img:icons/success.svg',
        iconColor: 'primary',
        iconSize: '20px',
      });
      // Reload tags
      reloadTags();
    }
  } catch (err) {
    if (isAxios401(err)) {
      refreshPage();
      return;
    }
    // Close dialog
    editTagDialog.value = false;
    // Show error message
    $q.notify({
      classes: 'q-px-lg',
      position: notifyPosition($q),
      progress: true,
      message: t('tag.update.error'),
      color: 'white',
      textColor: 'info',
      icon: 'img:icons/error.svg',
      iconColor: 'primary',
      iconSize: '20px',
    });
  }
}

function onShowEditTagDialog() {
  // Focus on cancel
  cancelEditTagBtn.value?.$el.focus();
}

function askConfirmationToRemoveTag(tag: TagDto) {
  $q.dialog({
    title: t('dialog.confirm'),
    message: t('tag.delete.confirm'),
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
    deleteTag(tag);
  });
}

async function deleteTag(tag: TagDto) {
  try {
    const res: AxiosResponse<TagDto> = await api.delete('/tags/' + tag.uuid);
    if (res.status === 204) {
      $q.notify({
        classes: 'q-px-lg',
        position: notifyPosition($q),
        progress: true,
        message: t('tag.delete.ok'),
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
      message: t('tag.delete.error'),
      color: 'white',
      textColor: 'info',
      icon: 'img:icons/error.svg',
      iconColor: 'primary',
      iconSize: '20px',
    });
  } finally {
    // Instead of preventing the removal of the QChip, reload tags so that if there is a failure the QChip will appear again
    // Reload tags
    reloadTags();
  }
}
</script>
<style>
.tag-name-input {
  min-width: 280px;
}
</style>
