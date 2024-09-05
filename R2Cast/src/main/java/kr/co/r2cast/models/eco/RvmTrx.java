package kr.co.r2cast.models.eco;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.utils.Util;

@Entity
@Table(name="ECO_RVM_TRXS", uniqueConstraints = {
          @javax.persistence.UniqueConstraint(columnNames = {"SITE_ID", "RVM_ID", "OP_DT"})
})
public class RvmTrx {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "rvm_trx_seq_gen")
    @SequenceGenerator(name = "rvm_trx_seq_gen", sequenceName = "ECO_RVM_TRXS_SEQ")
    @Column(name = "RVM_TRX_ID")
    private int id;

    
    /*
    <DateTime>2022/10/19 17:32:18</DateTime>	
    <TransactionNo>208</TransactionNo>	
    <ReceiptNo>208</ReceiptNo>	
    <OverallAmount>2360</OverallAmount>	
    <Barcodes>9800208023600</Barcodes>
    */
    
    // 수거일(시간 미포함): 특정일 기준 자료 query 목적
    @Column(name = "OP_DATE", nullable = false)
    private Date opDate;
	
    // 수거일시(시간포함) - XML / DateTime 저장
    @Column(name = "OP_DT", nullable = false)
    private Date opDt;

    // RVM의 트랜잭션 번호(RVM trx 일련 번호) - XML / TransactionNo 저장
    @Column(name = "TRX_NO", nullable = false)
    private int trxNo;

    // 영수증 번호 - XML / ReceiptNo 저장
    @Column(name = "RECEIPT_NO", nullable = false)
    private int receiptNo;

    // 영수증 총금액 - XML / OverallAmount 저장
    @Column(name = "OVERALL_AMOUNT", nullable = false)
    private int overallAmount;

    // 바코드 - XML / Barcodes 저장
    @Column(name = "BARCODES", length = 50)
    private String barcodes;

    
    // 유형 수
	@Transient
	private int groupCount;
    

    @Column(name = "CREATION_DATE", nullable = false)
    private Date whoCreationDate;
	
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SITE_ID", nullable = false)
    private Site site;
    
    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RVM_ID", nullable = false)
	private Rvm rvm;

    
    @OneToMany(mappedBy = "rvmTrx", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<RvmTrxItem> rvmTrxItems = new HashSet<RvmTrxItem>(0);
    
    
    
    public RvmTrx() {}
    
    public RvmTrx(Rvm rvm, Date opDt, int trxNo, int receiptNo, int overallAmount, String barcodes) {
    	
    	this.site = rvm.getSite();
    	this.rvm = rvm;
    	this.opDt = opDt;
    	this.opDate = Util.removeTimeOfDate(opDt);
    	this.trxNo = trxNo;
    	this.receiptNo = receiptNo;
    	this.overallAmount = overallAmount;
    	this.barcodes = barcodes;
    	
		this.whoCreationDate = new Date();
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getOpDate() {
		return opDate;
	}

	public void setOpDate(Date opDate) {
		this.opDate = opDate;
	}

	public Date getOpDt() {
		return opDt;
	}

	public void setOpDt(Date opDt) {
		this.opDt = opDt;
	}

	public int getTrxNo() {
		return trxNo;
	}

	public void setTrxNo(int trxNo) {
		this.trxNo = trxNo;
	}

	public int getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(int receiptNo) {
		this.receiptNo = receiptNo;
	}

	public int getOverallAmount() {
		return overallAmount;
	}

	public void setOverallAmount(int overallAmount) {
		this.overallAmount = overallAmount;
	}

	public String getBarcodes() {
		return barcodes;
	}

	public void setBarcodes(String barcodes) {
		this.barcodes = barcodes;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public Rvm getRvm() {
		return rvm;
	}

	public void setRvm(Rvm rvm) {
		this.rvm = rvm;
	}

	public int getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(int groupCount) {
		this.groupCount = groupCount;
	}

	@JsonIgnore
	public Set<RvmTrxItem> getRvmTrxItems() {
		return rvmTrxItems;
	}

	public void setRvmTrxItems(Set<RvmTrxItem> rvmTrxItems) {
		this.rvmTrxItems = rvmTrxItems;
	}
	
}
