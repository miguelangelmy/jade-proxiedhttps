package de.unidue.stud.sehawagn.pingpongagent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class PingPongAgent extends Agent {

	private static final long serialVersionUID = 545183917384169212L;
	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	private String receiverMTP;
	private String receiverAID;

	protected void setup() {
		super.setup();

		AnswerPingBehaviour pingPongBehaviour = new AnswerPingBehaviour(this);
		addBehaviour(pingPongBehaviour);

		Object[] args = getArguments();
		if(args!= null && args.length == 2) {
			receiverAID = args[0].toString();
			receiverMTP = args[1].toString();
			addBehaviour(new ServePingRequest(this, null));
		}
		System.out.println("Started PingPongAgent with receiverAID=" + receiverAID + ", receiverMTP=" + receiverMTP);
	}

	private class ServePingRequest extends OneShotBehaviour {

		private static final long serialVersionUID = 7057896873215275465L;
		private ACLMessage msg;

		public ServePingRequest(Agent a, ACLMessage msg) {
			super(a);
			this.msg = msg;
		}

		@Override
		public void action() {
			int waitTime = 1000;
			if (msg == null) {
				msg = new ACLMessage(ACLMessage.REQUEST);
				AID receiver = new AID(receiverAID, true);
				receiver.addAddresses(receiverMTP);
				msg.addReceiver(receiver);
				waitTime = 15000;
			}

			msg.setPerformative(ACLMessage.REQUEST);
			msg.setContent("ping");
			System.out.println("Waiting " + waitTime);

			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException | IllegalMonitorStateException e) {
				e.printStackTrace();
			}
			send(msg);
			System.out.println("Sent 'ping' msg " + msg + " to " + receiverAID + " via " + receiverMTP + ": \n" + msg);
		}
	}

	private class AnswerPingBehaviour extends CyclicBehaviour {

		private static final long serialVersionUID = -7364797017048617806L;

		public AnswerPingBehaviour(Agent a) {
			super(a);
		}

		public void action() {
			ACLMessage msg = myAgent.receive();
			if (msg != null) {
				ACLMessage reply = msg.createReply();

				if (msg.getPerformative() == ACLMessage.REQUEST) {
					String content = msg.getContent();
					if ((content != null) && (content.indexOf("ping") != -1)) {
						myLogger.log(Logger.INFO, "Agent " + getName() + " - Received ping from " + msg.getSender().getName() + ": \n" + msg);
						reply.setPerformative(ACLMessage.INFORM);
						reply.setContent("pong");
					} else {
						myLogger.log(Logger.INFO, "Agent " + getName() + " - Unexpected request [" + content + "] received from " + msg.getSender().getName() + ": \n" + msg);
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContent("( UnexpectedContent (" + content + "))");
					}

				} else if (msg.getPerformative() == ACLMessage.INFORM) {
					myLogger.log(Logger.INFO, "Agent " + getName() + " - Received pong INFORM from " + msg.getSender().getName() + ": \n" + msg);
					addBehaviour(new ServePingRequest(myAgent, reply));
					reply = null;
					block();
				} else {
					myLogger.log(Logger.INFO, "Agent " + getName() + " - Unexpected message [" + ACLMessage.getPerformative(msg.getPerformative()) + "] received from " + msg.getSender().getName() + ": \n" + msg);
					reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
					reply.setContent("( (Unexpected-act " + ACLMessage.getPerformative(msg.getPerformative()) + ") )");
				}
				if (reply != null) {
					send(reply);
					System.out.println("Sent reply msg :" + ": \n" + reply);
				}
			} else {
				System.out.println("Blocking AnswerPingBehaviour");
				block();
			}
		}
	}
}
