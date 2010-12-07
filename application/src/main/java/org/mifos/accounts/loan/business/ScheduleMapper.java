package org.mifos.accounts.loan.business;

import org.mifos.accounts.loan.schedule.domain.Installment;
import org.mifos.accounts.loan.schedule.domain.InstallmentPayment;
import org.mifos.accounts.loan.schedule.domain.Schedule;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;

import java.math.BigDecimal;
import java.util.*;

public class ScheduleMapper {

    public Schedule mapToSchedule(Collection<LoanScheduleEntity> loanScheduleEntities, Date disbursementDate, Double dailyInterestRate, BigDecimal loanAmount) {
        return new Schedule(disbursementDate, dailyInterestRate, loanAmount, mapToInstallments(loanScheduleEntities));
    }

    public void populateExtraInterestInLoanScheduleEntities(Schedule schedule, Map<Integer, LoanScheduleEntity> loanScheduleEntities) {
        for (Installment installment : schedule.getInstallments().values()) {
            LoanScheduleEntity loanScheduleEntity = loanScheduleEntities.get(installment.getId());
            loanScheduleEntity.setExtraInterest(new Money(loanScheduleEntity.getCurrency(), installment.getExtraInterest()));
        }
    }

    private List<Installment> mapToInstallments(Collection<LoanScheduleEntity> loanScheduleEntities) {
        List<Installment> installments = new ArrayList<Installment>();
        for (LoanScheduleEntity loanScheduleEntity : loanScheduleEntities) {
            installments.add(mapToInstallment(loanScheduleEntity));
        }
        return installments;
    }

    private Installment mapToInstallment(LoanScheduleEntity loanScheduleEntity) {
        Installment installment = new Installment(loanScheduleEntity.getInstallmentId().intValue(),
                loanScheduleEntity.getActionDate(), loanScheduleEntity.getPrincipal().getAmount(),
                loanScheduleEntity.getInterest().getAmount(), loanScheduleEntity.getExtraInterest().getAmount(),
                loanScheduleEntity.getTotalFees().getAmount(), loanScheduleEntity.getMiscFee().getAmount(),
                loanScheduleEntity.getPenalty().getAmount(), loanScheduleEntity.getMiscPenalty().getAmount());
        if (loanScheduleEntity.isPaymentApplied()) {
            installment.addPayment(getInstallmentPayment(loanScheduleEntity));
        }
        return installment;
    }

    private InstallmentPayment getInstallmentPayment(LoanScheduleEntity loanScheduleEntity) {
        InstallmentPayment installmentPayment = new InstallmentPayment();
        installmentPayment.setPaidDate(loanScheduleEntity.getPaymentDate());
        installmentPayment.setPrincipalPaid(loanScheduleEntity.getPrincipalPaid().getAmount());
        installmentPayment.setInterestPaid(loanScheduleEntity.getInterestPaid().getAmount());
        installmentPayment.setExtraInterestPaid(loanScheduleEntity.getExtraInterestPaid().getAmount());
        installmentPayment.setFeesPaid(loanScheduleEntity.getTotalFeesPaid().getAmount());
        installmentPayment.setMiscFeesPaid(loanScheduleEntity.getMiscFeePaid().getAmount());
        installmentPayment.setPenaltyPaid(loanScheduleEntity.getPenaltyPaid().getAmount());
        installmentPayment.setMiscPenaltyPaid(loanScheduleEntity.getMiscPenaltyPaid().getAmount());
        return installmentPayment;
    }

    public void populatePaymentDetails(Schedule schedule, LoanBO loanBO) {
        
    }
}