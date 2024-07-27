export default {
  daily: {
    error: {
      default: 'Unexpected error',
      password: {
        change: {
          compromised:
            'The new password is compromised and should not be used. Choose a different one and retry',
          generic: 'Error saving the new password',
          mismatch: 'The chosed passwords do not match',
        },
      },
    },
  },
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
  pages: {
    notFound: {
      text: 'Ooops, something went wrong',
      button: 'Home',
    },
    notes: {
      title: 'Notes',
      subtitle: 'Take note of things you want to remember',
    },
    tags: {
      title: 'Tags',
      subtitle:
        'Labels to associate to better distinguish entities. Touch a tag to edit it',
    },
    settings: {
      title: 'Settings',
      subtitle: 'Options are automatically saved',
    },
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
  tag: {
    name: {
      placeholder: 'Write the tag text here, then save it',
    },
    colorHex: {
      placeholder: 'Choose the color',
    },
    save: {
      button: 'Save',
      error: 'Error saving tag',
      ok: 'Tag correctly saved',
      validation: {
        name: {
          empty: 'The tag name cannot be empty',
        },
        colorHex: {
          empty: 'The tag color cannot be empty',
        },
      },
    },
    update: {
      error: 'Error updating tag',
      ok: 'Tag correctly updated',
      dialog: {
        title: 'Update tag',
        confirm: 'Confirm',
        cancel: 'Cancel',
      },
      confirm:
        'If the tag is linked with one or more entities, the change will be propagated. Are you sure you want to continue?',
    },
    delete: {
      error: 'Error deleting tag',
      ok: 'Tag correctly deleted',
      confirm:
        'If you delete the tag, every association to entities will be deleted as well. Are you sure you want to continue?',
    },
    addToNote: {
      error: 'Error linking tag to note',
      ok: 'Tag correctly linked to note',
      placeholder: 'Search for tags to add',
      noResults: 'No tags available found',
    },
    removeFromNote: {
      error: 'Error removing tag from note',
      ok: 'Tag correctly removed from note',
    },
    loading: {
      error: 'Error loading tags',
    },
  },
};
