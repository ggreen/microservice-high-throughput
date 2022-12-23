package com.linkedIn.learning.throughput.domain;

import java.sql.Timestamp;

public record Transaction(String id, String details,String contact, String location, double amount, Timestamp timestamp) {
}
