import java.io.*;

import java.util.ArrayList;
import java.util.*;

import java.text.*;
import java.math.*;
import java.util.regex.*;

import javax.swing.plaf.synth.SynthSpinnerUI;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class banker implements Cloneable {

	public static ArrayList<Integer> make_list(ArrayList<Integer> resources) throws CloneNotSupportedException  {
		ArrayList<Integer> new_list = new ArrayList<>();
		for(int i = 0; i< new_list.size();i+=1) {
			int temp = resources.get(i);
			new_list.add(temp);
		}
		
		return new_list;
	}
	public static void handle_deadlock(int num_tasks,Map<Integer,Integer> task_blocked,Map<Integer,Integer> blocked_resource, Map<Integer,Integer> blocked_amt,int[][] jobs, int num_resource,ArrayList<Integer> temp_resource,Map<Integer,Integer> aborted_task,Map<Integer,Integer> resources ) {
		for(int i = 1; i<num_tasks+1; i+=1) {
			
			if(task_blocked.get(i) != null) {
				if(task_blocked.get(i) == 1) {
					int resource_number =  blocked_resource.get(i);
					int blocked_amount = blocked_amt.get(i);
					int current_amt = jobs[i-1][resource_number-1];
					
					//loop of aborted task giving up all its resources
					
					System.out.println("job");
					System.out.println(i);
					System.out.println("Is aborting and giving up its resources");
					
					for(int j = 1; j<num_resource+1;j+=1) {
						temp_resource.set(j-1, temp_resource.get(j-1)+jobs[i-1][j-1]);
						jobs[i-1][j-1] = 0;
						aborted_task.put(i, 1);
					}
					
					
					//for loop checking if deadlock is fixed
					for(int j = 1; j < num_tasks+1; j+=1) {
						
						if(task_blocked.get(j) != null) {
							if(task_blocked.get(j) == 1 && aborted_task.get(j) == 0) {
								int res_num = blocked_resource.get(j);
								int blocked_am = blocked_amt.get(j);
								if(res_num == resource_number) {
									System.out.println("the combined is");
									System.out.println(temp_resource.get(res_num-1)+resources.get(res_num));
									System.out.println("trying to get");
									System.out.println(blocked_am);
									System.out.println("there is enough");
									
									if(temp_resource.get(res_num-1)+resources.get(res_num) >= blocked_am) {
										
										return;
										
										
									}
								}
								else {
									
								}
							}
							}
					}
				}
				
			}
			
				
		}
		
	}
	
	public static boolean check_safe(int[][] jobs,Map<Integer,ArrayList<Integer>> jobs_claim,Map<Integer,Integer> resources, int resource, int amt, int job) {
		System.out.println("The manager is starting with");
		System.out.println(resources.get(1));
		System.out.println("Check safe is being run");
		int current = jobs[job-1][resource-1];
		jobs[job-1][resource-1] = current+amt;
		int current_manage = resources.get(resource);
		resources.put(resource, current_manage-amt);
		for(int i =1; i < jobs_claim.size()+1;i+=1) {
			int safe_counter = 0;
			
			for(int j = 1;j<resources.size()+1;j+=1) {
				System.out.println("The jobs claim");
				System.out.println(jobs_claim.get(i).get(j-1));
				System.out.println("the jobs current resources");
				System.out.println(jobs[i-1][j-1]);
				int to_finish = jobs_claim.get(i).get(j-1)-jobs[i-1][j-1];
				System.out.println("the job being examined");
				System.out.println(i);
				System.out.println("The job needs this much to finish");
				System.out.println(to_finish);
				System.out.println("The resource manager has this much");
				System.out.println(resources.get(j));
				System.out.println("\n");
				
				if(to_finish <= resources.get(j)) {
					safe_counter += 1;
				}
			}
			
			
			if(safe_counter >= resources.size()) {
				int current2 = jobs[job-1][resource-1];
				int current_manage2 = resources.get(resource);
				jobs[job-1][resource-1] =  current2-amt;
				resources.put(resource, current_manage2+amt);
				return true;
			}
		}
		int current2 = jobs[job-1][resource-1];
		int current_manage2 = resources.get(resource);
		jobs[job-1][resource-1] =  current2-amt;
		resources.put(resource, current_manage2+amt);
		
		return false;
	}
	
	public static void main(String[] args)throws FileNotFoundException,CloneNotSupportedException {
		System.out.println("Enter the name of the file you would like to analyze");
		Scanner user = new Scanner(System.in);
		String the_file = user.nextLine();
		int algo;
		System.out.println("Enter the algorithm you woud like to run 1 for fifo 2 for bankers algorithm");
		algo = user.nextInt();
		File file = new File(the_file);
		Scanner input = new Scanner(file);
		int num_tasks = input.nextInt();
		int num_resource = input.nextInt();
		//hash maps for storing all of the info on each task
		Map<Integer,Integer> jobs_resources = new HashMap();
		Map<Integer,Integer> action_job = new HashMap();
		Map<Integer,String> action_type = new HashMap();
		Map<Integer,Integer> action_time = new HashMap();
		Map<Integer,Integer> action_resource = new HashMap();
		Map<Integer,Integer> action_amt = new HashMap();
		Map<Integer,Integer> action_complete = new HashMap();
		
		Map<Integer,Integer> resources = new HashMap();
		
		//putting resources with their amounts into hashmap
		for(int i =1; i<num_resource+1;i+=1 ) {
			resources.put(i, input.nextInt());
			
		}
		//initialize list of resources with zero amounts to be put into jobs 
		
		ArrayList<Integer> temp_resource = new ArrayList<>();
		ArrayList<Integer> resource_claim_list = new ArrayList<>();
		ArrayList<Integer> resource_list = new ArrayList<>();
		for(int i = 1; i<num_resource+1;i+=1) {
			resource_list.add(0);
			temp_resource.add(0);
			resource_claim_list.add(0);
		}
		//hashmap to keep track of resources job actually has
		
		//hashmap to keep track of resources job claims
		Map<Integer,ArrayList<Integer>> jobs_claim = new HashMap();
		
		Map<Integer,Integer> task_blocked = new HashMap();
		Map<Integer,Integer> blocked_resource = new HashMap();
		Map<Integer,Integer> blocked_amt = new HashMap();
		Map<Integer,Integer> completed_task = new HashMap();
		Map<Integer,Integer> blocked_action_num = new HashMap();
		Map<Integer,Integer> aborted_task = new HashMap();
		Map<Integer,Integer> waiting = new HashMap();
		Map<Integer,Integer> total = new HashMap();
		for(int i =1; i<num_tasks+1;i+=1 ) {
			
			
			
			
			jobs_claim.put(i, resource_claim_list);
			task_blocked.put(i, 0);
			blocked_resource.put(i, 0);
			blocked_amt.put(i, 0);
			aborted_task.put(i, 0);
			completed_task.put(i, 0);
			waiting.put(i, 0);
			total.put(i, 0);
			
			
		}
		
		int action_num  = 1;
		String type;
		int job;
		int time;
		int resource;
		int amt;
		//loading all the information into the appropriate hashmap
		int[][] jobs = new int[num_tasks][num_resource];
		for(int i = 0;i<num_tasks;i+=1) {
			for(int j = 0; j< num_resource; j+=1) {
				jobs[i][j] = 0;
			}
		}
		while(input.hasNext()) {
			
			type = input.next();
			job = input.nextInt();
			time = input.nextInt();
			resource = input.nextInt();
			amt = input.nextInt();
			if(type.equals("initiate")) {
				
				jobs_claim.get(job).set(resource-1, amt);
				
				
				
			}
			
			
			action_type.put(action_num, type);
			action_job.put(action_num, job);
			action_time.put(action_num, time);
			action_resource.put(action_num,resource);
			action_amt.put(action_num, amt);
			action_complete.put(action_num, 0);
			
			
			action_num += 1;
			
			
			
		}
		
		input.close();
		
		int cycles = 0;
		int total_waiting = 0;
		
		ArrayList<Integer> blocked = new ArrayList<>();
		if(algo == 1) {
			
			
			boolean completed = false;
			while(completed == false) {
				
				int blocked_counter = 0;
				for(int i = 1;i<num_tasks+1;i+=1) {
					if(task_blocked.get(i) != null) {
						if(task_blocked.get(i) == 1 && aborted_task.get(i) == 0) {
							
							waiting.put(i, waiting.get(i)+1);
							total.put(i, total.get(i)+1);
							total_waiting += 1;
							
							blocked_counter += 1;
							int resource_number =  blocked_resource.get(i);
							int blocked_amount = blocked_amt.get(i);
							int blocked_action = blocked_action_num.get(i);
							if(resources.get(resource_number) >= blocked_amount) {
								int current_amt = jobs[i-1][resource_number-1];
								 int current_manager = resources.get(resource_number);
								 jobs[i-1][resource_number-1] = current_amt+blocked_amount;
								 resources.put(resource_number, current_manager-blocked_amount);
								 System.out.println("blocked task");
								 System.out.println(i);
								 System.out.println("made a request for resource");
								 System.out.println(resource_number);
								 System.out.println("of units");
								 System.out.println(blocked_amount);
								 System.out.println("And it was granted");
								 System.out.println("\n");
								 action_complete.put(blocked_action, 1);
								 task_blocked.put(i, 0);
							}
							else {
								System.out.println("blocked task");
								 System.out.println(i);
								 System.out.println("made a request for resource");
								 System.out.println(resource_number);
								 System.out.println("of units");
								 System.out.println(blocked_amount);
								 System.out.println("And it was not granted");
								 System.out.println("\n");
								 
								
							}
						}	
					}
					
				}//end of for loop for blocked tasks
				//deadlock checking and handling code
				int completed_counter = 0;
				int aborted_counter = 0;
				for(int j = 1;j<num_tasks+1;j+=1) {
					if(completed_task.get(j) != null) {
					if(completed_task.get(j) == 1) {
						completed_counter += 1;
					}
					}
					if(aborted_task.get(j) != null) {
						if(aborted_task.get(j) == 1) {
							aborted_counter +=1;
						}
					}
					
				}
				if(num_tasks-completed_counter-aborted_counter == blocked_counter) {
					boolean deadlock = true;
					handle_deadlock( num_tasks, task_blocked, blocked_resource,  blocked_amt, jobs,  num_resource, temp_resource, aborted_task, resources);
					
					while(deadlock == false) {
						
						
						for(int i = 1; i<num_tasks+1; i+=1) {
							
							if(task_blocked.get(i) != null) {
								if(task_blocked.get(i) == 1) {
									int resource_number =  blocked_resource.get(i);
									int blocked_amount = blocked_amt.get(i);
									int current_amt = jobs[i-1][resource_number-1];
									
									//loop of aborted task giving up all its resources
									
									System.out.println("job");
									System.out.println(i);
									System.out.println("Is aborting and giving up its resources");
									
									for(int j = 1; j<num_resource+1;j+=1) {
										temp_resource.set(j-1, temp_resource.get(j-1)+jobs[i-1][j-1]);
										jobs[i-1][j-1] = 0;
										aborted_task.put(i, 1);
									}
									
									
									//for loop checking if deadlock is fixed
									for(int j = 1; j < num_tasks+1; j+=1) {
										
										if(task_blocked.get(j) != null) {
											if(task_blocked.get(j) == 1 && aborted_task.get(j) == 0) {
												int res_num = blocked_resource.get(j);
												int blocked_am = blocked_amt.get(j);
												if(res_num == resource_number) {
													System.out.println("the combined is");
													System.out.println(temp_resource.get(res_num-1)+resources.get(res_num));
													System.out.println("trying to get");
													System.out.println(blocked_am);
													System.out.println("there is enough");
													
													if(temp_resource.get(res_num-1)+resources.get(res_num) >= blocked_am) {
														
														deadlock = false;
														
														
													}
												}
												else {
													
												}
											}
											}
									}
								}
								
							}
							
								
						}// end of for loop
					}// end of while deadlock loop
					completed_counter = 0;
					blocked_counter = 0;
					aborted_counter = 0;
				}// end of if statement
				else {
					completed_counter = 0;
					blocked_counter = 0;
					aborted_counter = 0;
				}
				//end of deadlock checking and handling code
				boolean do_action;
				for(int i = 1; i < num_tasks+1;i+=1) {
					//variable for keeping track of a task already has an action going
					//setting do_action to true because this is beginning of job
					do_action = true;
					//checking that job is not blocked or completed
					if(task_blocked.get(i) == 0 && completed_task.get(i) == 0 && aborted_task.get(i) == 0) {
					for(int j = 1; j<action_num;j+=1) {
						//checking that action matched the job we want and that this action has not been completed
						if(action_job.get(j) == i && action_complete.get(j) == 0) {
							
							if(do_action == true) {
								//handling of initiate actoin
								if(action_type.get(j).equals("initiate")) {
									total.put(i, total.get(i)+1);
									if(action_time.get(j) == 0) {
										int resource_num = action_resource.get(j);
										int resource_request = action_amt.get(j);
										System.out.println(i);
										System.out.println("has initiated");
										System.out.println(resource_request);
										System.out.println("with");
										System.out.println(resource_num);
										System.out.println("units");
										System.out.println("\n");
										action_complete.put(j, 1);
										
										
									}
									else {
										action_time.put(j, action_time.get(j)-1);
										
									}
								}
								//handling of request action
								if(action_type.get(j).equals("request")) {
									total.put(i, total.get(i)+1);
									if(action_time.get(j) == 0) {
										//this is the resource number requested by the current job
										 int resource_num = action_resource.get(j);
										 //getting the amount of the resource requested by the current job
										 int resource_request = action_amt.get(j);
										 //if the resource has enough grand request
										 
										 if(resources.get(resource_num) >= resource_request) {
											 
											 int current_amt = jobs[i-1][resource_num-1];
											 int current_manager = resources.get(resource_num);
											 jobs[i-1][resource_num-1] = current_amt+resource_request;
											 resources.put(resource_num, current_manager-resource_request);
											 do_action = false;
											 System.out.println("task");
											 System.out.println(i);
											 System.out.println("made a request for resource");
											 System.out.println(resource_num);
											 System.out.println("And it was granted");
											 System.out.println("\n");
											 action_complete.put(j, 1);
										 }
										 else {
											 task_blocked.put(i, 1);
											 blocked_resource.put(i, resource_num);
												blocked_amt.put(i, resource_request);
												blocked_action_num.put(i, j);
												System.out.println("task");
												 System.out.println(i);
												 System.out.println("made a request for resource");
												 System.out.println(resource_num);
												 System.out.println("And it was denied");
												 System.out.println("\n");
										 }
									}
									else {
										action_time.put(j, action_time.get(j)-1);
										
										
										
									}
									
								}
								//handling of release action
								if(action_type.get(j).equals("release")) {
									total.put(i, total.get(i)+1);
									if(action_time.get(j) == 0) {
										int resource_num = action_resource.get(j);
										int resource_request = action_amt.get(j);
										int current_amt = jobs[i-1][resource_num-1];
										temp_resource.set(resource_num-1, temp_resource.get(resource_num-1)+resource_request);    
										jobs[i-1][resource_num-1] = current_amt-resource_request;
										System.out.println(i);
										System.out.println("has released");
										System.out.println(resource_request);
										System.out.println("with");
										System.out.println(resource_num);
										System.out.println("units");
										System.out.println("\n");
										action_complete.put(j, 1);
										
									}
									else {
										action_time.put(j, action_time.get(j)-1);
										
									}
									
								}
								//handling of terminate action
								if(action_type.get(j).equals("terminate")) {
									if(action_time.get(j) == 0) {
										int resource_num = action_resource.get(j);
										int resource_request = action_amt.get(j);
										System.out.println(i);
										System.out.println("has terminated");
										System.out.println(resource_request);
										System.out.println("with");
										System.out.println(resource_num);
										System.out.println("units");
										System.out.println("\n");
										action_complete.put(j, 1);
										completed_task.put(i, 1);
										
									}
									else {
										action_time.put(j, action_time.get(j)-1);
										
									}
									
								}
							do_action = false;	
							}
						}
					}// end of for loop action num
					}
				}//end of for loop num tasks
				cycles+=1;
				//releasing released resources to the manager at the end so they are available next time
				for(int j = 0; j< temp_resource.size() ;j+=1) {
					int release = temp_resource.get(j);
					int manage = resources.get(j+1);
					resources.put(j+1, manage+release);
					temp_resource.set(j, 0);
				}
				//checking if jobs are completed
				int complete_counter = 0;
				for(int j = 1;j<num_tasks+1;j+=1) {
					if(completed_task.get(j) == 1) {
						complete_counter += 1;
					}
					if(aborted_task.get(j) != null) {
						if(aborted_task.get(j) == 1) {
							aborted_counter +=1;
						}
					}
				}
				if(complete_counter+aborted_counter == num_tasks) {
					completed = true;
				}
				else {
					complete_counter = 0;
					aborted_counter = 0;
				}
			}//end of while loop
			float wait = 0;
			float tot = 0;
			System.out.println("FIFO");
			for(int i =1;i<num_tasks+1;i+=1) {
				if(aborted_task.get(i) == 1) {
					System.out.println("Task "+i+"\taborted");
				}
				else {
					float the_total = total.get(i);
					float the_wait = waiting.get(i);
					float percent = (the_wait/the_total);
					
					System.out.println("Task" +i+"\t"+ the_total+"\t"+the_wait+ "\t"+percent);
					tot += total.get(i);
					wait += waiting.get(i);
				}
			}
			System.out.println("total\t"+tot+"\t"+wait+"\t"+(wait/tot));
		}//end of if statement
		if(algo == 2) {
			boolean completed = false;
while(completed == false) {
	
				int blocked_counter = 0;
				for(int i = 1;i<num_tasks+1;i+=1) {
					if(task_blocked.get(i) != null) {
						if(task_blocked.get(i) == 1) {
							waiting.put(i, waiting.get(i)+1);
							total.put(i, total.get(i)+1);
							total_waiting += 1;
							
							blocked_counter += 1;
							int resource_number =  blocked_resource.get(i);
							int blocked_amount = blocked_amt.get(i);
							int blocked_action = blocked_action_num.get(i);
							if(resources.get(resource_number) >= blocked_amount && check_safe(jobs,jobs_claim,resources,resource_number,blocked_amount,i) == true) {
								int current_amt = jobs[i-1][resource_number-1];
								 int current_manager = resources.get(resource_number);
								 jobs[i-1][resource_number-1] = current_amt+blocked_amount;
								 resources.put(resource_number, current_manager-blocked_amount);
								 System.out.println("blocked task");
								 System.out.println(i);
								 System.out.println("made a request for resource");
								 System.out.println(resource_number);
								 System.out.println("of units");
								 System.out.println(blocked_amount);
								 System.out.println("And it was granted");
								 System.out.println("\n");
								 action_complete.put(blocked_action, 1);
								 task_blocked.put(i, 0);
							}
							else {
								System.out.println("blocked task");
								 System.out.println(i);
								 System.out.println("made a request for resource");
								 System.out.println(resource_number);
								 System.out.println("of units");
								 System.out.println(blocked_amount);
								 System.out.println("And it was not granted");
								 System.out.println("\n");
								 
								
							}
						}	
					}
					
				}//end of for loop for blocked tasks
				//deadlock checking and handling code
				
				//end of deadlock checking and handling code
				boolean do_action;
				for(int i = 1; i < num_tasks+1;i+=1) {
					//variable for keeping track of a task already has an action going
					//setting do_action to true because this is beginning of job
					do_action = true;
					//checking that job is not blocked or completed
					if(task_blocked.get(i) == 0 && completed_task.get(i) == 0) {
					for(int j = 1; j<action_num;j+=1) {
						//checking that action matched the job we want and that this action has not been completed
						if(action_job.get(j) == i && action_complete.get(j) == 0) {
							
							if(do_action == true) {
								//handling of initiate actoin
								if(action_type.get(j).equals("initiate")) {
									total.put(i, total.get(i)+1);
									if(action_time.get(j) == 0) {
										int resource_num = action_resource.get(j);
										int resource_request = action_amt.get(j);
										System.out.println(i);
										System.out.println("has initiated");
										System.out.println(resource_request);
										System.out.println("with");
										System.out.println(resource_num);
										System.out.println("units");
										System.out.println("\n");
										action_complete.put(j, 1);
										
										
										
									}
									else {
										action_time.put(j, action_time.get(j)-1);
										
										
									}
								}
								//handling of request action
								if(action_type.get(j).equals("request")) {
									total.put(i, total.get(i)+1);
									if(action_time.get(j) == 0) {
										//this is the resource number requested by the current job
										 int resource_num = action_resource.get(j);
										 //getting the amount of the resource requested by the current job
										 int resource_request = action_amt.get(j);
										 //if the resource has enough grand request
										 System.out.println(resources.get(resource_num));
										 if(resources.get(resource_num) >= resource_request && check_safe(jobs,jobs_claim,resources,resource_num,resource_request,i) == true) {
											 int current_amt = jobs[i-1][resource_num-1];
											 int current_manager = resources.get(resource_num);
											 jobs[i-1][resource_num-1] = current_amt+resource_request;
											 resources.put(resource_num, current_manager-resource_request);
											 do_action = false;
											 System.out.println("task");
											 System.out.println(i);
											 System.out.println("made a request for resource");
											 System.out.println(resource_num);
											 System.out.println("And it was granted");
											 System.out.println("\n");
											 action_complete.put(j, 1);
										 }
										 else {
											 task_blocked.put(i, 1);
											 blocked_resource.put(i, resource_num);
												blocked_amt.put(i, resource_request);
												blocked_action_num.put(i, j);
												System.out.println("task");
												 System.out.println(i);
												 System.out.println("made a request for resource");
												 System.out.println(resource_num);
												 System.out.println("And it was denied");
												 System.out.println("\n");
										 }
									}
									else {
										action_time.put(j, action_time.get(j)-1);
										
										
										
									}
									
								}
								//handling of release action
								if(action_type.get(j).equals("release")) {
									total.put(i, total.get(i)+1);
									if(action_time.get(j) == 0) {
										int resource_num = action_resource.get(j);
										int resource_request = action_amt.get(j);
										int current_amt = jobs[i-1][resource_num-1];
										temp_resource.set(resource_num-1, temp_resource.get(resource_num-1)+resource_request);    
										jobs[i-1][resource_num-1] =current_amt-resource_request;
										System.out.println(i);
										System.out.println("has released");
										System.out.println(resource_request);
										System.out.println("with");
										System.out.println(resource_num);
										System.out.println("units");
										System.out.println("\n");
										action_complete.put(j, 1);
										
									}
									else {
										action_time.put(j, action_time.get(j)-1);
										
									}
									
								}
								//handling of terminate action
								if(action_type.get(j).equals("terminate")) {
									if(action_time.get(j) == 0) {
										int resource_num = action_resource.get(j);
										int resource_request = action_amt.get(j);
										System.out.println(i);
										System.out.println("has terminated");
										System.out.println(resource_request);
										System.out.println("with");
										System.out.println(resource_num);
										System.out.println("units");
										System.out.println("\n");
										action_complete.put(j, 1);
										completed_task.put(i, 1);
										
									}
									else {
										action_time.put(j, action_time.get(j)-1);
										
									}
									
								}
							do_action = false;	
							}
						}
					}// end of for loop action num
					}
				}//end of for loop num tasks
				cycles+=1;
				//releasing released resources to the manager at the end so they are available next time
				for(int j = 0; j< temp_resource.size() ;j+=1) {
					int release = temp_resource.get(j);
					int manage = resources.get(j+1);
					resources.put(j+1, manage+release);
					temp_resource.set(j, 0);
				}
				//checking if jobs are completed
				int complete_counter = 0;
				for(int j = 1;j<num_tasks+1;j+=1) {
					if(completed_task.get(j) == 1) {
						complete_counter += 1;
					}
					if(aborted_task.get(j) != null) {
						if(aborted_task.get(j) == 1) {
							
						}
					}
				}
				if(complete_counter == num_tasks) {
					completed = true;
				}
				else {
					complete_counter = 0;
					
				}
				
				
			}//end of while loop
			float wait = 0;
			float tot = 0;
			System.out.println("Bankers");
			for(int i =1;i<num_tasks+1;i+=1) {
				if(aborted_task.get(i) == 1) {
					System.out.println("Task "+i+"\taborted");
				}
				else {
					float the_total = total.get(i);
					float the_wait = waiting.get(i);
					float percent = (the_wait/the_total);
					
					System.out.println("Task" +i+"\t"+ the_total+"\t"+the_wait+ "\t"+percent);
					tot += total.get(i);
					wait += waiting.get(i);
				}
			}
			System.out.println("total\t"+tot+"\t"+wait+"\t"+(wait/tot));
			
		}//end of if statement
		
		
	}
}
