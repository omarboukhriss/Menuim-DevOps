# ProGuard rules for the TableOrder app.
# Not needed for debug builds, but useful if you ever create a release APK.
# We keep this file empty for now — add rules here if ProGuard removes
# something it shouldn't (e.g., Gson model classes need to keep their fields).

# Keep Gson model fields from being removed/renamed:
# -keep class com.tableorder.model.** { *; }
