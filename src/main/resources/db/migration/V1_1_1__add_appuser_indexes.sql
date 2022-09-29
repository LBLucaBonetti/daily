-- Add indexes for the app_user fields in both note and tag tables
CREATE INDEX idx_note_appuser ON note(app_user);

CREATE INDEX idx_tag_appuser ON tag(app_user);