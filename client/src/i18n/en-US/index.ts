export default {
  language: {
    label: 'Language',
    english: 'English',
    italian: 'Italian',
  },
  dialog: {
    confirm: 'Confirm',
    cancel: 'Cancel',
    save: 'Save',
    edit: 'Edit',
    delete: 'Delete',
  },
  note: {
    placeholder: 'Write something here, then save it',
    save: {
      button: 'Save',
      error: 'Error saving note',
      ok: 'Note correctly saved',
      beingEdited:
        'You currently have notes being edited. If you proceed, you will lose your changes to them. Choose <span class="text-1 text-weight-medium">OK</span> to discard changes and save the note or <span class="text-1 text-weight-medium">CANCEL</span> to keep editing them and avoid saving this note',
      validation: {
        empty: 'The note cannot be empty',
      },
    },
    delete: {
      confirm: 'Do you really want to delete the note?',
      error: 'Error deleting note',
      ok: 'Note correctly deleted',
    },
    loading: {
      error: 'Error loading notes',
    },
    created: 'Created',
  },
};
