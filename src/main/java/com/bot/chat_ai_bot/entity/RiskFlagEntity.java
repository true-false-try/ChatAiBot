package com.bot.chat_ai_bot.entity;

import com.bot.chat_ai_bot.utils.JsonMapConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "risk_flag")
@NoArgsConstructor
@AllArgsConstructor
public class RiskFlagEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    BigInteger id;

    @ManyToOne
    @JoinColumn(name ="user_id")
    UserEntity user;

    @Convert(converter = JsonMapConverter.class)
    Map<String, Object> flag;

    @Column(name = "triggered_at")
    Long triggeredAt;
}
