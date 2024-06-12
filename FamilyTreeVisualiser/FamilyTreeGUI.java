import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class FamilyTreeGUI extends JFrame {
    private JTextField queryField;
    private JLabel resultLabel;

    public FamilyTreeGUI() {
        setTitle("Family Tree");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Convert the binary tree to DefaultMutableTreeNode
        DefaultMutableTreeNode root = createTreeNodes(FamilyTree.root);
        // Create a JTree
        JTree tree = new JTree(root);

        // Add the tree to a scroll pane
        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for queries
        JPanel queryPanel = new JPanel();
        queryPanel.setLayout(new GridLayout(6, 2));

        // Query input field and buttons
        queryField = new JTextField();
        queryPanel.add(new JLabel("Enter Person's Name:"));
        queryPanel.add(queryField);

        JButton siblingButton = new JButton("Find Sibling");
        siblingButton.addActionListener(new QueryActionListener("sibling"));
        queryPanel.add(siblingButton);

        JButton cousinButton = new JButton("Find Cousins");
        cousinButton.addActionListener(new QueryActionListener("cousin"));
        queryPanel.add(cousinButton);

        JButton parentButton = new JButton("Find Parent");
        parentButton.addActionListener(new QueryActionListener("parent"));
        queryPanel.add(parentButton);

        JButton grandParentButton = new JButton("Find Grandparent");
        grandParentButton.addActionListener(new QueryActionListener("grandparent"));
        queryPanel.add(grandParentButton);

        JButton childrenButton = new JButton("Find Children");
        childrenButton.addActionListener(new QueryActionListener("children"));
        queryPanel.add(childrenButton);

        JButton uncleAuntButton = new JButton("Find Uncle/Aunt");
        uncleAuntButton.addActionListener(new QueryActionListener("uncleAunt"));
        queryPanel.add(uncleAuntButton);

        resultLabel = new JLabel();
        queryPanel.add(resultLabel);

        add(queryPanel, BorderLayout.SOUTH);
    }

    private DefaultMutableTreeNode createTreeNodes(FamilyTree.Person person) {
        if (person == null) {
            return null;
        }
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(person.name + " (" + person.gender + ")");
        if (person.firstChild != null) {
            node.add(createTreeNodes(person.firstChild));
        }
        if (person.secondChild != null) {
            node.add(createTreeNodes(person.secondChild));
        }
        return node;
    }

    private class QueryActionListener implements ActionListener {
        private String queryType;

        public QueryActionListener(String queryType) {
            this.queryType = queryType;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String personName = queryField.getText().trim();
            if (personName.isEmpty()) {
                resultLabel.setText("Please enter a person's name.");
                return;
            }

            String result = "";
            switch (queryType) {
                case "sibling":
                    FamilyTree.Person sibling = FamilyTree.Sibling(FamilyTree.root, personName);
                    result = (sibling != null) ? "Sibling: " + sibling.name : "No sibling found.";
                    break;
                case "cousin":
                    ArrayList<String> cousins = new ArrayList<>(FamilyTree.Cousins(FamilyTree.root, personName));
                    result = (!cousins.isEmpty()) ? "Cousins: " + String.join(", ", cousins) : "No cousins found.";
                    break;
                case "parent":
                    FamilyTree.Person parent = FamilyTree.Parent(FamilyTree.root, personName);
                    result = (parent != null) ? "Parent: " + parent.name : "No parent found.";
                    break;
                case "grandparent":
                    FamilyTree.Person grandparent = FamilyTree.findGrandParent(FamilyTree.root, personName);
                    result = (grandparent != null) ? "Grandparent: " + grandparent.name : "No grandparent found.";
                    break;
                case "children":
                    ArrayList<String> children = new ArrayList<>(FamilyTree.findChildren(FamilyTree.root, personName));
                    result = (!children.isEmpty()) ? "Children: " + String.join(", ", children) : "No children found.";
                    break;
                case "uncleAunt":
                    FamilyTree.Person uncleAunt = FamilyTree.findUncleOrAunt(FamilyTree.root, personName);
                    result = (uncleAunt != null) ? "Uncle/Aunt: " + uncleAunt.name : "No uncle or aunt found.";
                    break;
            }
            resultLabel.setText(result);
        }
    }

    public static void main(String[] args) {
        // Create the family tree
        FamilyTree.addPerson("Vikram", 'M', "Aman", 'M');
        FamilyTree.addPerson("Vikram", 'M', "Rohan", 'M');
        FamilyTree.addPerson("Aman", 'M', "Aditi", 'F');
        FamilyTree.addPerson("Aman", 'M', "Ishan", 'M');
        FamilyTree.addPerson("Rohan", 'M', "Ram", 'M');
        FamilyTree.addPerson("Rohan", 'M', "Meera", 'F');
        // Create and show the GUI
        SwingUtilities.invokeLater(() -> {
            FamilyTreeGUI frame = new FamilyTreeGUI();
            frame.setVisible(true);
        });
    }
}

class FamilyTree {
    public static Person root = null;

    static class Person {
        String name;
        char gender;
        Person firstChild;
        Person secondChild;

        public Person(String name, char gender) {
            this.name = name;
            this.gender = gender;
            this.firstChild = null;
            this.secondChild = null;
        }
    }

    public static void addPerson(String parentName, char parentGender, String childName, char childGender) {
        if (root == null) {
            Person person = new Person(parentName, parentGender);
            root = person;
            Person child = new Person(childName, childGender);
            person.firstChild = child;
        } else {
            Person temp = findPerson(root, parentName);
            if (temp != null) {
                if (temp.firstChild == null) {
                    temp.firstChild = new Person(childName, childGender);
                } else if (temp.secondChild == null) {
                    temp.secondChild = new Person(childName, childGender);
                }
            }
        }
    }

    public static Person findPerson(Person root, String name) {
        if (root == null || root.name.equals(name)) {
            return root;
        }
        Person firstChildRes = findPerson(root.firstChild, name);
        if (firstChildRes == null) {
            return findPerson(root.secondChild, name);
        }
        return firstChildRes;
    }

    public static Person Parent(Person root, String name) {
        if (root == null) {
            return null;
        }
        if ((root.firstChild != null && root.firstChild.name.equals(name)) || (root.secondChild != null && root.secondChild.name.equals(name))) {
            return root;
        }
        Person res = Parent(root.firstChild, name);
        if (res != null) {
            return res;
        }
        return Parent(root.secondChild, name);
    }

    public static Person Sibling(Person root, String name) {
        if (root == null) {
            return null;
        }
        Person parent = Parent(root, name);
        if (parent == null) {
            return null;
        }
        if (parent.firstChild != null && parent.firstChild.name.equals(name) && parent.secondChild != null) {
            return parent.secondChild;
        } else if (parent.secondChild != null && parent.secondChild.name.equals(name) && parent.firstChild != null) {
            return parent.firstChild;
        } else {
            return null;
        }
    }

    public static ArrayList<String> Cousins(Person root, String name) {
        ArrayList<String> cousins = new ArrayList<>();
        Person parent = Parent(root, name);
        Person parentSibling = Sibling(root, parent.name);
        if (parentSibling != null) {
            if (parentSibling.firstChild != null) {
                cousins.add(parentSibling.firstChild.name);
            }
            if (parentSibling.secondChild != null) {
                cousins.add(parentSibling.secondChild.name);
            }
        }
        return cousins;
    }

    public static Person findGrandParent(Person root, String name) {
        Person parent = Parent(root, name);
        if (parent == null) {
            return null;
        }
        return Parent(root, parent.name);
    }

    public static ArrayList<String> findChildren(Person root, String name) {
        ArrayList<String> children = new ArrayList<>();
        Person person = findPerson(root, name);
        if (person != null) {
            if (person.firstChild != null) {
                children.add(person.firstChild.name);
            }
            if (person.secondChild != null) {
                children.add(person.secondChild.name);
            }
        }
        return children;
    }

    public static Person findUncleOrAunt(Person root, String name) {
        Person parent = Parent(root, name);
        if (parent == null) {
            return null;
        }
        return Sibling(root, parent.name);
    }
}
