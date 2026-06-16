package com.example.medicalmanagement.models;

public class Appointment {
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String patientName;
    private String doctorName;
    private String date;
    private String time;
    private String status;
    private String disease;
    private String prescription;
    private String progress;
    private double billAmount;
    private String billDescription;
    private boolean billGenerated;

    public Appointment() {
        // Empty constructor required by Firebase Firestore
    }

    public Appointment(String appointmentId, String patientId, String doctorId, String patientName, String doctorName, String date, String time, String status, String disease, String prescription, String progress, double billAmount, String billDescription, boolean billGenerated) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.date = date;
        this.time = time;
        this.status = status;
        this.disease = disease;
        this.prescription = prescription;
        this.progress = progress;
        this.billAmount = billAmount;
        this.billDescription = billDescription;
        this.billGenerated = billGenerated;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(double billAmount) {
        this.billAmount = billAmount;
    }

    public String getBillDescription() {
        return billDescription;
    }

    public void setBillDescription(String billDescription) {
        this.billDescription = billDescription;
    }

    public boolean isBillGenerated() {
        return billGenerated;
    }

    public void setBillGenerated(boolean billGenerated) {
        this.billGenerated = billGenerated;
    }
}
