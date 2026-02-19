package com.thesis.freelance.features.flag.entity;

import com.thesis.freelance.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "flag")
public class Flag extends BaseEntity {
}