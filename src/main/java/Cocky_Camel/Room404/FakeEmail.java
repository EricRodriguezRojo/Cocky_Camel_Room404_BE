package Cocky_Camel.Room404;

import jakarta.persistence.*;

@Entity // 
@Table(name = "fake_emails")
public class FakeEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String sender;
    
    @Column(columnDefinition = "TEXT")
    private String bodyText;

    @ManyToOne
  @JoinColumn(name = "puzzle_id")
 private Puzzle puzzle;

    public FakeEmail() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getBodyText() { return bodyText; }
    public void setBodyText(String bodyText) { this.bodyText = bodyText; }

   public Puzzle getPuzzle() { return puzzle; }
   public void setPuzzle(Puzzle puzzle) { this.puzzle = puzzle; }
}