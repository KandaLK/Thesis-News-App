package com.thesis.app.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.thesis.app.R;
public class AlertHelper {

    public interface DialogCallback {
        void onPositiveClick();
        void onNegativeClick();
    }

    public static void showLogoutDialog(Context context, LogoutCallback callback) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_logout_confirmation, null);

        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel);
        MaterialButton btnLogout = dialogView.findViewById(R.id.btn_logout);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (callback != null) {
                    callback.onCancel();
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (callback != null) {
                    callback.onConfirm();
                }
            }
        });

        dialog.setCancelable(false);

        dialog.show();
    }

    // Callback interface for handling logout confirmation
    public interface LogoutCallback {
        void onConfirm();
        void onCancel();
    }


    public static void showSuccessSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.success_green));
        snackbar.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));
        snackbar.show();
    }

    public static void showErrorSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.error_red));
        snackbar.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));
        snackbar.show();
    }

    public static void showInfoSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.primary_blue));
        snackbar.setTextColor(ContextCompat.getColor(view.getContext(), R.color.white));
        snackbar.show();
    }

    public static void showEditProfileDialog(Context context, String changesSummary, DialogCallback callback) {
        // Inflate custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_edit_confirmation, null);

        // Find views in the custom layout
        TextView tvChangesSummary = dialogView.findViewById(R.id.tv_changes_summary);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel);
        MaterialButton btnUpdate = dialogView.findViewById(R.id.btn_update);

        // Set changes summary text
        if (changesSummary != null && !changesSummary.trim().isEmpty()) {
            tvChangesSummary.setText(changesSummary);
            tvChangesSummary.setVisibility(View.VISIBLE);
        } else {
            tvChangesSummary.setText("No changes detected");
            tvChangesSummary.setVisibility(View.VISIBLE);
        }

        // Create dialog using MaterialAlertDialogBuilder[4]
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Set button click listeners[8]
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            if (callback != null) {
                callback.onNegativeClick();
            }
        });

        btnUpdate.setOnClickListener(v -> {
            dialog.dismiss();
            if (callback != null) {
                callback.onPositiveClick();
            }
        });

        // Handle dialog cancellation (back button or outside touch)
        dialog.setOnCancelListener(dialogInterface -> {
            if (callback != null) {
                callback.onNegativeClick();
            }
        });

        dialog.show();
    }
    public static void showDeleteAccountDialog(Context context, DialogCallback callback) {
        // Inflate custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_delete_account, null);

        // Find buttons in the custom layout
        MaterialButton btnCancel = dialogView.findViewById(R.id.btn_cancel);
        MaterialButton btnDelete = dialogView.findViewById(R.id.btn_delete);

        // Create dialog using MaterialAlertDialogBuilder
        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // Set button click listeners
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            if (callback != null) {
                callback.onNegativeClick();
            }
        });

        btnDelete.setOnClickListener(v -> {
            dialog.dismiss();
            if (callback != null) {
                callback.onPositiveClick();
            }
        });

        // Handle dialog cancellation (back button or outside touch)
        dialog.setOnCancelListener(dialogInterface -> {
            if (callback != null) {
                callback.onNegativeClick();
            }
        });

        dialog.show();
    }

    public static void showCancelConfirmationDialog(Context context, DialogCallback callback) {
        new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme)
                .setTitle("Cancel Profile Update")
                .setMessage("Are you sure you want to cancel? All unsaved changes will be lost.")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("Yes, Cancel!", (dialog, which) -> {
                    dialog.dismiss();
                    if (callback != null) {
                        callback.onPositiveClick();
                    }
                })
                .setNegativeButton("Continue", (dialog, which) -> {
                    dialog.dismiss();
                    if (callback != null) {
                        callback.onNegativeClick();
                    }
                })
                .setCancelable(true)
                .setOnCancelListener(dialogInterface -> {
                    if (callback != null) {
                        callback.onNegativeClick();
                    }
                })
                .show();
    }

}
